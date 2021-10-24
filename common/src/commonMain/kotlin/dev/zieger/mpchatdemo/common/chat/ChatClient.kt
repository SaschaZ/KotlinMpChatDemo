package dev.zieger.mpchatdemo.common.chat

import androidx.compose.runtime.MutableState
import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ChatClient(
    private val host: String,
    private val path: String,
    private val port: Int,
    private val errorState: MutableState<String?>,
    private val onNewContent: suspend (content: ChatContent) -> Unit
) {

    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 60_000
        }
        followRedirects = true
    }

    private val sendChannel = Channel<String>()
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startSocket(username: String, onInitialized: () -> Unit): Job = scope.launch {
        initializeClient(username) {
            onInitialized()
            scope.launch { sendAll(sendChannel) }
            receiveAll(onNewContent)
        }
    }

    fun sendMessage(msg: String) {
        scope.launch { sendChannel.send(msg) }
    }

    private suspend fun initializeClient(
        username: String,
        session: suspend DefaultClientWebSocketSession.() -> Unit
    ) {
        try {
            when (host.contains("localhost")) {
                false -> client.wss(path, request = {
                    host = this@ChatClient.host
                    port = this@ChatClient.port
                    parameter("username", username)
                }, session)
                true -> client.ws(path, request = {
                    host = this@ChatClient.host
                    port = this@ChatClient.port
                    parameter("username", username)
                }, session)
            }
        } catch (t: Throwable) {
            errorState.value = "$t"
        }
    }
}

suspend fun DefaultClientWebSocketSession.sendAll(values: ReceiveChannel<String>) {
    for (msg in values) send(msg)
}

private val json = Json { classDiscriminator = "#class" }

suspend fun DefaultClientWebSocketSession.receiveAll(listener: suspend ChatContent.() -> Unit) {
    for (frame in incoming) {
        when (frame) {
            is Frame.Text -> frame.readText().ifBlank { null }?.also {
                json.decodeFromString(ChatContent.serializer(), it).listener()
            }
            else -> Unit
        }
    }
}