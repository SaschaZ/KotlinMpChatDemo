package dev.zieger.mpchatdemo.common.chat

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zieger.mpchatdemo.common.Link
import dev.zieger.mpchatdemo.common.Table
import dev.zieger.mpchatdemo.common.TextField
import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.common.currentTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.foundation.layout.fillMaxHeight
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.*
import org.jetbrains.compose.common.ui.unit.dp
import org.jetbrains.compose.common.ui.unit.sp

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun Chat(
    host: String = "chat.zieger.dev",
    port: Int = 443,
    fontSize: Int = 25,
    timeFontSize: Int = 15,
    useDarkButtonColor: Boolean = false
) {
    // This model represents the UI of this composable.
    val model = remember { ChatModel() }

    // Create ChatClient instance and add every new message to the messages SnapShotStateList
    // of our model.
    val chat = remember {
        ChatClient(host, port, model.error, model.messages)
    }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(1f).background(Color.LightGray)) {
        // Switch the UI depending on the login/connecting state of the user.
        when {
            model.error.value != null -> ShowError(model.error, model, useDarkButtonColor)
            model.userName.value.isBlank() -> {
                loggedOut(model, chat, useDarkButtonColor)
                description()
            }
            model.isConnecting.value -> Text("connecting …")
            else -> LoggedIn(model, chat, fontSize, timeFontSize, useDarkButtonColor)
        }
    }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
private fun ShowError(
    errorState: MutableState<String?>,
    model: ChatModel,
    useDarkButtonColor: Boolean
) {
    Text(errorState.value!!)
    Button(onClick = {
        model.userName.value = ""
        errorState.value = null
    }) { Text("retry", color = if (useDarkButtonColor) Color.Black else Color.White) }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun loggedOut(
    model: ChatModel,
    chat: ChatClient,
    useDarkButtonColor: Boolean
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

    var focusRequester: () -> Unit = {}
    TextField(
        model.userName,
        onValueChange = {
            model.userName.value = it.filterNot { c -> c.isWhitespace() }
                .ifBlank { null }?.take(32) ?: ""
        },
        maxLines = 1,
        onSubmit = { login() },
        focusRequester = { focusRequester = it },
        label = { Text("Username: ") },
        button = {
            Button(onClick = { login() }) {
                Text("ENTER", color = if (useDarkButtonColor) Color.Black else Color.White)
            }
        }
    )

    DisposableEffect(Unit) {
        focusRequester()
        onDispose { }
    }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun LoggedIn(
    model: ChatModel,
    chat: ChatClient,
    fontSize: Int,
    timeFontSize: Int,
    useDarkButtonColor: Boolean
) {
    var focusRequester: () -> Unit = {}
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
            button = {
                Button(onClick = { send() }) {
                    Text("Send", color = if (useDarkButtonColor) Color.Black else Color.White)
                }
            },
            focusRequester = { focusRequester = it }
        )
    }
    ChatMessageList(model.messages, fontSize.sp, timeFontSize.sp)

    DisposableEffect(Unit) {
        focusRequester()
        onDispose { }
    }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun description() {
    Column(Modifier.padding(16.dp).fillMaxWidth().fillMaxHeight(1f)) {
        Row {
            Text(
                "Just enter a name of your choice and click the ENTER button to join the chat.",
                color = Color.DarkGray,
                modifier = Modifier.padding(8.dp)
            )
        }

        Text(
            "Available commands:", color = Color.Black,
            modifier = Modifier.padding(8.dp)
        )
        Table(Modifier.padding(24.dp)) {
            Tr {
                Td {
                    Tr {
                        Text(
                            "/color #[RGB-HEX]",
                            modifier = Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                    Tr {
                        Text(
                            "/me …",
                            modifier = Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                }
                Td {
                    Tr {
                        Text(
                            "- change the color of your name",
                            color = Color.DarkGray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Tr {
                        Text(
                            "- indirect speech",
                            color = Color.DarkGray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        Text("\nAvailable Platforms:", color = Color.Black, modifier = Modifier.padding(8.dp))
        Table(Modifier.padding(24.dp)) {
            Tr {
                Td {
                    Tr {
                        Text(
                            "Android",
                            Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                    Tr {
                        Text(
                            "Linux",
                            Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                    Tr {
                        Text(
                            "Mac",
                            Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                }
                val tag = remember { }
                Td {
                    Tr {
                        Text(
                            "Apk",
                            Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                    Tr {
                        Link(
                            "https://zieger.dev/files/MpChatDemo/$currentTag/MpChatDemo-linux-jvm-$currentTag.jar",
                            "Jar"
                        )
                    }
                    Tr {
                        Link(
                            "https://zieger.dev/files/MpChatDemo/$currentTag/MpChatDemo-mac-jvm-$currentTag.jar",
                            "Jar"
                        )
                    }
                }
                Td {
                    Tr {
                        Text(
                            "",
                            Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                    Tr {
                        Link(
                            "https://zieger.dev/files/MpChatDemo/$currentTag/MpChatDemo-linux-native-$currentTag.deb",
                            "Deb"
                        )
                    }
                    Tr {
                        Link(
                            "https://zieger.dev/files/MpChatDemo/$currentTag/MpChatDemo-mac-native-$currentTag.dmg",
                            "Dmi"
                        )
                    }
                }
            }
        }
    }
}

data class ChatModel(
    val userName: MutableState<String> = mutableStateOf(""),
    val isConnecting: MutableState<Boolean> = mutableStateOf(false),
    // current entered (and not send yet) message of the user
    val userMessage: MutableState<String> = mutableStateOf(""),
    // all received messages off all users
    val messages: SnapshotStateList<ChatContent> = mutableStateListOf(),
    val error: MutableState<String?> = mutableStateOf(null)
)