package dev.zieger.mpchatdemo.server_socket

import dev.zieger.mpchatdemo.common.Constants.PORT
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.css.*
import kotlinx.html.*
import java.text.SimpleDateFormat
import java.util.*

fun HTML.username() {
    head {
        title("KotlinMpChatDemo")
        link(rel = "stylesheet", href = "/styles.css", type = "text/css")
    }
    body {
        div { id = "root" }
        script(src = "/static/web.js") {}
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
//            install(DefaultHeaders)

            fun String.fixSlash(): String = when {
                this == "/" -> this
                endsWith("/") -> removeSuffix("/")
                else -> this
            }

            routing {
                get("/") {
                    call.respondHtml(HttpStatusCode.OK, HTML::username)
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