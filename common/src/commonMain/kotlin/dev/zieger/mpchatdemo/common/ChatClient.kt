package dev.zieger.mpchatdemo.common

import dev.zieger.mpchatdemo.common.dto.ChatContent
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ChatClient(
    private val url: Url,
    private val onNewContent: ChatContent.() -> Unit
) {

    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 60_000
        }
    }

    private val sendChannel = Channel<String>()
    private val scope = CoroutineScope(Dispatchers.Default)

    private fun CoroutineScope.startWebSocket(username: String) = launch {
        val json = Json { classDiscriminator = "#class" }
        val session: suspend DefaultClientWebSocketSession.() -> Unit = {
            launch { for (msg in sendChannel) send(msg) }

            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> frame.readText().ifBlank { null }?.also {
                        json.decodeFromString(ChatContent.serializer(), it).onNewContent()
                    }
                    else -> Unit
                }
            }
        }

        when (url.host.contains("localhost")) {
            false -> client.wss(url.encodedPath, request = {
                host = this@ChatClient.url.host
                port = this@ChatClient.url.port
                parameter("username", username)
            }, session)
            true -> client.ws(url.encodedPath, request = {
                host = this@ChatClient.url.host
                port = this@ChatClient.url.port
                parameter("username", username)
            }, session)
        }
    }

    fun startSocket(username: String, sendMessage: ((String) -> Unit) -> Unit = {}): Job {
        val job = scope.startWebSocket(username)
        sendMessage { msg -> sendMessage(msg) }
        return job
    }

    fun sendMessage(message: String) {
        scope.launch { sendChannel.send(message) }
    }
}