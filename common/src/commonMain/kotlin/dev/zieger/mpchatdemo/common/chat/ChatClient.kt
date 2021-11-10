package dev.zieger.mpchatdemo.common.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zieger.mpchatdemo.common.Constants.WEB_SOCKET_PING_INTERVAL
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatClient(
    private val host: String,
    private val port: Int,
    private val errorState: MutableState<String?>,
    private val messageList: SnapshotStateList<ChatContent>
) {

    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = WEB_SOCKET_PING_INTERVAL * 1000L
        }
        followRedirects = true
    }

    private val sendChannel = Channel<String>()
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startSocket(username: String, onInitialized: () -> Unit): Job = scope.launch {
        initializeClient(username) {
            onInitialized()
            scope.launch { sendAll(sendChannel) }
            receiveAll(messageList)
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
                false -> client.wss("/", request = {
                    host = this@ChatClient.host
                    port = this@ChatClient.port
                    parameter("username", username)
                }, session)
                true -> client.ws("/", request = {
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

suspend fun DefaultClientWebSocketSession.receiveAll(messageList: SnapshotStateList<ChatContent>) {
    for (frame in incoming) {
        when (frame) {
            is Frame.Text -> frame.readText().ifBlank { null }?.also {
                messageList.add(Json.decodeFromString(it))
            }
            else -> Unit
        }
    }
}