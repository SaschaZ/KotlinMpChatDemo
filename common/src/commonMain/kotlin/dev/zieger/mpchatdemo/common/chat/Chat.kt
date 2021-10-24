package dev.zieger.mpchatdemo.common.chat

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zieger.mpchatdemo.common.TextField
import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.sp

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun Chat(
    host: String = "chat.zieger.dev",
    port: Int = 443,
    path: String = "/",
    fontSize: Int = 25
) {
    // This model represents the UI of this composable.
    val model = remember { ChatModel() }
    // Create ChatClient instance and add every new message to the messages SnapShotStateList
    // of our model.
    val chat = remember {
        ChatClient(host, path, port) { content -> model.messages.add(0, content) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Switch the UI depending on the login/connecting state of the user.
        when {
            model.userName.value.isBlank() -> loggedOut(model, chat)
            model.isConnecting.value -> Text("connecting …")
            else -> LoggedIn(model, chat, fontSize)
        }
    }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun loggedOut(
    model: ChatModel,
    chat: ChatClient
) = Row {
    val scope = rememberCoroutineScope { Dispatchers.Default }

    fun login() {
        model.isConnecting.value = true
        scope.launch {
            chat.startSocket(model.userName.value) {
                model.isConnecting.value = false
            }
        }
    }

    TextField(
        model.userName,
        onValueChange = {
            model.userName.value = it.filterNot { c -> c.isWhitespace() }
                .ifBlank { null }?.take(32) ?: ""
        },
        maxLines = 1,
        onSubmit = { login() },
        focusRequester = {},
        label = { Text("Username: ") }
    )
    Button(onClick = { login() }) { Text("Ok") }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun LoggedIn(
    model: ChatModel,
    chat: ChatClient,
    fontSize: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        fun send() {
            chat.sendMessage(model.userMessage.value)
            model.userMessage.value = ""
        }

        TextField(
            model.userMessage,
            onValueChange = {
                model.userMessage.value = it
            },
            onSubmit = { send() },
            label = { Text("Message: ") },
            focusRequester = {}
        )
        Button(onClick = { send() }) { Text("Send") }
    }
    ChatMessageList(model.messages, fontSize.sp)
}

data class ChatModel(
    val userName: MutableState<String> = mutableStateOf(""),
    val isConnecting: MutableState<Boolean> = mutableStateOf(false),
    // current entered (and not send yet) message of the user
    val userMessage: MutableState<String> = mutableStateOf(""),
    // all received messages off all users
    val messages: SnapshotStateList<ChatContent> = mutableStateListOf()
)