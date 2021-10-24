package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.Constants.PORT
import dev.zieger.mpchatdemo.common.dto.ChatContent
import dev.zieger.mpchatdemo.common.dto.ChatContent.Message
import dev.zieger.mpchatdemo.common.dto.ChatContent.Notification
import io.ktor.application.*
import io.ktor.features.*
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
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

fun HTML.username(path: String) {
    head {
        title("KotlinMpChatDemo")
        link(rel = "stylesheet", href = "${path}styles.css", type = "text/css")
    }
    body {
        div { id = "root" }
        script(src = "${path}static/web.js") {}
    }
}

fun CSSBuilder.style() {
    body {
        backgroundColor = Color.lightGray
        margin(5.px)
    }
    rule("root") {
        width = 100.vw - 10.px
        display = Display.flex
    }
}

fun main(args: Array<String>) {
    org.apache.log4j.BasicConfigurator.configure()

    val port = args.indexOf("-p").takeIf { it in 0 until args.lastIndex }
        ?.let { args[it + 1].toIntOrNull() } ?: PORT
    val path = args.indexOf("--path").takeIf { it in 0 until args.lastIndex }?.let { args[it + 1] }
        ?.let { if (it.endsWith("/")) it else "$it/" } ?: "/"

    val env = applicationEngineEnvironment {
//        log = YOUR_LOGGER
        module {
            install(CallLogging) {
                level = org.slf4j.event.Level.DEBUG
            }
            install(StatusPages) {
                exception<Throwable> { ex ->
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "${ex.javaClass.simpleName} - ${ex.message ?: "Empty Message"}"
                    )
                }
            }
            install(DefaultHeaders)
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(60)
            }

            val scope = CoroutineScope(Dispatchers.IO)
            val messageChannel = Channel<ChatContent>()
            val messages =
                flow { emitAll(messageChannel) }.shareIn(scope, SharingStarted.Eagerly, 1024)
            val json = Json { classDiscriminator = "#class" }

            scope.launch { ChatContents.all().forEach { messageChannel.send(it) } }

            suspend fun sendContent(content: ChatContent) {
                ChatContents.add(content)
                messageChannel.send(content)
            }

            fun String.fixSlash(): String = when {
                this == "/" -> this
                endsWith("/") -> removeSuffix("/")
                else -> this
            }

            routing {
                webSocket(path.fixSlash()) {
                    call.parameters["username"]?.also { u ->
                        println("new user $u connected")
                        val user = Users.getOrInsert(u)

                        val sendJob = scope.launch {
                            messages.collect {
                                send(json.encodeToString(ChatContent.serializer(), it))
                            }
                        }

                        val key = System.currentTimeMillis()
                        sendContent(
                            Notification(
                                user,
                                key, key.format(),
                                "joined the chat"
                            )
                        )

                        for (frame in incoming) when (frame) {
                            is Frame.Text -> frame.readText().ifBlank { null }?.also { msg ->
                                sendContent(Message(user, key, key.format(), msg))
                            }
                            is Frame.Ping,
                            is Frame.Pong -> Unit
                            is Frame.Close ->
                                sendContent(
                                    Notification(
                                        user, key, key.format(),
                                        "left chat"
                                    )
                                )
                            else -> throw IllegalArgumentException(
                                "Unknown Frame type " + "${frame::class}"
                            )
                        }
                        sendJob.cancelAndJoin()
                    } ?: run<Unit> {
                        send(
                            Frame.Close(
                                CloseReason(
                                    CloseReason.Codes.CANNOT_ACCEPT.code,
                                    "No username provided!"
                                )
                            )
                        )
                    }
                }

                get(path.fixSlash()) {
                    call.respondHtml(HttpStatusCode.OK) { username(path) }
                }
                get("$path/styles.css") {
                    call.respondCss { style() }
                }
                static("$path/static") {
                    resources()
                }
            }
        }

        connector {
            host = "0.0.0.0"
            this.port = port
        }
    }
    embeddedServer(Netty, env).start(wait = true)
}

fun Long.format(): String =
    SimpleDateFormat("dd.MM.-HH:mm:ss").format(Date(this))

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}