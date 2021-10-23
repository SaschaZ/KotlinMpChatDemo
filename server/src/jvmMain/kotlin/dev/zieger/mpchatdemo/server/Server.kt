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

fun HTML.username() {
    head {
        title("KotlinMpChatDemo")
        link(rel = "stylesheet", href = "/styles.css", type = "text/css")
        script(src = "$/static/web.js") {}
    }
    body {
        h1 {
            +"KotlinMpChatDemo"
        }

        div {
            id = "root"

            form {
                id = "usernameForm"

                +"Username: "
                textInput {
                    id = "username"
                    autoComplete = false
                }

                submitInput {
                    hidden = true
                }
            }
        }
    }
}

fun CSSBuilder.style() {
    body {
        backgroundColor = Color.darkGreen
        margin(5.px)
    }
    rule("h1") {
        color = Color.yellow
    }
    rule("root") {
        width = 100.vw - 10.px
        display = Display.flex
    }
    rule("form") {
        color = Color.yellow
        flexGrow = 1.0
        display = Display.flex
    }
    rule("input") {
        backgroundColor = Color.black
        color = Color.white
        flexGrow = 1.0
    }
    rule("textarea") {
        backgroundColor = Color.black
        resize = Resize.none
        color = Color.white
        width = 100.vw - 17.px
        height = 68.vh
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
                level = org.slf4j.event.Level.INFO
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

            routing {
                webSocket("/") {
                    call.parameters["username"]?.also { user ->
                        println("new user: $user")

                        val sendJob = scope.launch {
                            messages.collect {
                                println("new message for $user: $it")
                                send(json.encodeToString(ChatContent.serializer(), it))
                            }
                        }

                        sendContent(
                            Notification(
                                System.currentTimeMillis(), System.currentTimeMillis().format(),
                                "\"$user\" joined the chat"
                            )
                        )

                        for (frame in incoming) when (frame) {
                            is Frame.Text -> frame.readText().ifBlank { null }?.also { msg ->
                                sendContent(
                                    Message(
                                        user,
                                        System.currentTimeMillis(),
                                        System.currentTimeMillis().format(),
                                        msg
                                    )
                                )
                            }
                            is Frame.Ping,
                            is Frame.Pong -> Unit
                            is Frame.Close ->
                                sendContent(
                                    Notification(
                                        System.currentTimeMillis(),
                                        System.currentTimeMillis().format(),
                                        "\"$user\" left chat"
                                    )
                                )
                            else -> throw IllegalArgumentException(
                                "Unknown Frame type " +
                                        "${frame::class}"
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

                get(path) {
                    call.respondHtml(HttpStatusCode.OK) { username() }
                }
                get("/styles.css") {
                    call.respondCss { style() }
                }
                static("/static") {
                    resources()
                }
            }
        }

        connector {
            host = "127.0.0.1"//"0.0.0.0"
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