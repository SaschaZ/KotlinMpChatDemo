package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.Constants.INTERNAL_HOST
import dev.zieger.mpchatdemo.common.Constants.PATH
import dev.zieger.mpchatdemo.common.Constants.PORT
import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType.MESSAGE
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType.NOTIFICATION
import dev.zieger.mpchatdemo.server.db.ChatContents
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
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


fun main(args: Array<String>) {
    org.apache.log4j.BasicConfigurator.configure()

    val port = args.getOrNull(args.indexOf("-p") + 1)?.toIntOrNull() ?: PORT
    val path = args.getOrNull(args.indexOf("--path") + 1) ?: PATH

    embeddedServer(Netty, applicationEngineEnvironment {
        connector {
            host = INTERNAL_HOST
            this.port = port
        }

        module {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(60)
            }

            val scope = CoroutineScope(Dispatchers.IO)
            // This Channel will be used to send new ChatContent.
            val messageChannel = Channel<ChatContent>()
            // Emit all messageChannel values into a flow and transform it to a SharedFlow
            // that will be used to receive the ChatContent for each connected user.
            val messages = flow { emitAll(messageChannel) }
                .shareIn(scope, SharingStarted.Eagerly, 1024)
            // Get stored content from database and send it to the messageChannel.
            scope.launch { ChatContents.all().forEach { messageChannel.send(it) } }

            // The routing block is invoked for all received http requests.
            routing {
                // Is invoked when a new user has connected to the websocket.
                webSocket("/") {
                    handleClientConnection(scope, messages, messageChannel)
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

private val json = Json { classDiscriminator = "#class" }

private suspend fun DefaultWebSocketServerSession.handleClientConnection(
    scope: CoroutineScope,
    messages: SharedFlow<ChatContent>,
    messageChannel: Channel<ChatContent>
) {
    suspend fun sendContent(content: ChatContent) {
        // Add every new message to the database …
        ChatContents.add(content)
        // … and send it with our message channel to all other users.
        messageChannel.send(content)
    }

    call.parameters["username"]?.also { u ->
        println("new user $u connected")
        val user = Users.getOrInsert(u)

        // Collect all messages and send them to the connected user
        val sendJob = scope.launch {
            messages.collect {
                send(json.encodeToString(ChatContent.serializer(), it))
            }
        }

        // Send "user joined" notification
        val initialKey = System.currentTimeMillis()
        sendContent(
            ChatContent(NOTIFICATION, user, initialKey, initialKey.format(), "joined the chat")
        )

        for (frame in incoming) when (frame) {
            is Frame.Text -> {
                // User send a new message
                val key = System.currentTimeMillis()
                frame.readText().ifBlank { null }?.also { msg ->
                    sendContent(ChatContent(MESSAGE, user, key, key.format(), msg))
                }
            }
            is Frame.Ping,
            is Frame.Pong -> Unit
            is Frame.Close -> {
                // User disconnected
                val key = System.currentTimeMillis()
                sendContent(
                    ChatContent(NOTIFICATION, user, key, key.format(), "left chat")
                )
            }
            else -> throw IllegalArgumentException(
                "Unknown Frame type " + "${frame::class}"
            )
        }
        sendJob.cancelAndJoin()
    } ?: handleUserNameNotProvided()
}

private suspend fun DefaultWebSocketServerSession.handleUserNameNotProvided() =
    send(
        Frame.Close(
            CloseReason(CloseReason.Codes.CANNOT_ACCEPT.code, "No username provided!")
        )
    )

fun Long.format(): String =
    SimpleDateFormat("dd.MM.-HH:mm:ss", Locale.getDefault()).format(Date(this))

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}