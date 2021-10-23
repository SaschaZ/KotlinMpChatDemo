package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zieger.mpchatdemo.common.dto.ChatContent
import io.ktor.http.*
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.*
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.TextUnit
import org.jetbrains.compose.common.ui.unit.sp

@InternalCoroutinesApi
@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun Chat(url: String, port: Int) = Chat(Url("$url:$port"))

@InternalCoroutinesApi
@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun Chat(url: Url) {
    val fontSize: TextUnit = 25.sp
    val model = remember { ChatModel() }
    val scope = rememberCoroutineScope()
    val chat = remember {
        ChatClient(url) {
            scope.launch { model.messages.add(0, this@ChatClient) }
        }
    }

    val requestFocus = remember { mutableStateOf({}) }
    Column(modifier = Modifier.fillMaxWidth()) {
        when {
            model.userName.value == null -> Row {
                val username = remember { mutableStateOf("") }
                fun login() {
                    model.userName.value = username.value
                    model.isConnecting.value = true
                    scope.launch {
                        chat.startSocket(model.userName.value!!) {
                            model.isConnecting.value = false
                        }
                    }
                }
                TextField(username,
                    onValueChange = {
                        username.value = it.filterNot { c -> c.isWhitespace() }
                            .ifBlank { null }?.take(15) ?: ""
                    },
                    singleLine = true,
                    onSubmit = { login() },
                    focusRequester = { requestFocus.value = it },
                    label = { Text("Username: ") }
                )
                Button(onClick = { login() }) {
                    Text("Ok")
                }
            }
            model.isConnecting.value -> Text("connecting …")
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val message = remember { mutableStateOf("") }
                    fun send() {
                        chat.sendMessage(message.value)
                        message.value = ""
                    }
                    TextField(
                        message,
                        onValueChange = {
                            if (it.last() == '\n') send()
                            else message.value = it
                        },
                        onSubmit = { send() },
                        label = {},
                        focusRequester = {}
                    )
                    Button(onClick = { send() }) {
                        Text("Send")
                    }
                }
                ChatMessageList(model.messages, fontSize)
            }
        }
    }

    DisposableHandle {
        requestFocus.value()
    }
}

data class ChatModel(
    val userName: MutableState<String?> = mutableStateOf(null),
    val isConnecting: MutableState<Boolean> = mutableStateOf(false),
    val messages: SnapshotStateList<ChatContent> = mutableStateListOf()
)