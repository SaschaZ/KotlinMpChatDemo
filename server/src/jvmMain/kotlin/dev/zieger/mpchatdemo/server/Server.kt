package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.server.db.DbMessageBridge
import dev.zieger.mpchatdemo.server.db.Users
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.css.CSSBuilder
import kotlinx.html.HTML
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.*

class Server(
    private val host: String,
    private val port: Int,
    private val path: String = "/"
) {
    private val json = Json { classDiscriminator = "#class" }

    suspend fun start() {
        embeddedServer(Netty, applicationEngineEnvironment {
            connector {
                host = this@Server.host
                port = this@Server.port
            }

            module {
                install(WebSockets) {
                    pingPeriod = Duration.ofSeconds(60)
                }

                val scope = CoroutineScope(Dispatchers.IO)
                // This Channel will be used to send new ChatContent.
                val firstStage = Channel<ChatContent>()
                val secondStage = DbMessageBridge(scope, firstStage)
                val finalStageChannel = ChatBot(scope, secondStage, firstStage)
                val finalStageFlow = flow { emitAll(finalStageChannel) }
                    .shareIn(scope, SharingStarted.Eagerly, 128)

                // The routing block is invoked for all received http requests.
                routing {
                    // Is invoked when a new user has connected to the websocket.
                    webSocket("/") {
                        handleClientConnection(scope, finalStageFlow, firstStage)
                    }


                    // Is invoked for all http calls to the root address. We will serve a HTML page
                    // with a root div  tag that will be used by jetpack compose to render the content.
                    get("/") {
                        call.respondHtml(HttpStatusCode.OK, HTML::rootHtml)
                    }

                    // Stylesheet file for the root HTML page.
                    get("/styles.css") {
                        call.respondCss(CSSBuilder::rootCss)
                    }

                    // Serve all files from the resources folder. The build process will put the
                    // generated JavaScript of the web module inside here.
                    static("/static") {
                        resources()
                    }
                }
            }
        }).start(wait = true)
    }

    private suspend fun DefaultWebSocketServerSession.handleClientConnection(
        scope: CoroutineScope,
        messages: Flow<ChatContent>,
        messageChannel: Channel<ChatContent>
    ) {
        call.parameters.getOrNull("username")?.also { u ->
            println("new user $u connected")
            val user = Users.getOrInsert(u)

            // Collect all messages and send them to the connected user
            val sendJob = scope.launch {
                messages.collect {
                    send(json.encodeToString(ChatContent.serializer(), it))
                }
            }

            // Send "user joined" notification
            messageChannel.sendNotification(user, "joined the chat")

            for (frame in incoming) when (frame) {
                is Frame.Text -> {
                    Users.get(user.name)?.also {
                        // User send a new message
                        frame.readText().ifBlank { null }?.also { msg ->
                            messageChannel.sendMessage(it, msg)
                        }
                    }
                }
                is Frame.Ping,
                is Frame.Pong -> Unit
                is Frame.Close -> {
                    Users.get(user.name)?.also {
                        // User disconnected
                        messageChannel.sendNotification(it, "left chat")
                    }
                }
                else -> throw IllegalArgumentException(
                    "Unknown Frame type " + "${frame::class}"
                )
            }
            sendJob.cancelAndJoin()
        } ?: handleUserNameNotProvided()
    }

    private fun Parameters.getOrNull(name: String): String? = try {
        getOrFail<String>(name)
    } catch (t: Throwable) {
        null
    }

    private suspend fun DefaultWebSocketServerSession.handleUserNameNotProvided() =
        send(
            Frame.Close(
                CloseReason(CloseReason.Codes.CANNOT_ACCEPT.code, "No username provided!")
            )
        )

    private suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
        this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
    }
}