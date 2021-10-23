import dev.zieger.mpchatdemo.common.ChatClient
import dev.zieger.mpchatdemo.common.dto.ChatContent
import dev.zieger.mpchatdemo.common.dto.ChatContent.*
import io.ktor.http.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.append
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main(args: Array<String>) {
    val port = 9020
    val path = "/"

//    println("after main")
//    renderComposable(rootElementId = "root") {
////        Chat()
//        println("root found")
//        Text("Hello")
//    }

    val url = Url(
        URLProtocol.HTTP, "localhost", port, path,
        Parameters.Empty, "", null, null, false
    )
    val chat = ChatClient(url) { append() }
    var username: String? = null
    window.onload = {
        document.getElementById("usernameForm")?.let { form ->
            form.onSubmit {
                (document.getElementById("username") as HTMLInputElement).apply {
                    username = value
                    form.remove()

                    (document.getElementById("root") as HTMLElement).chat()
                    document.getElementById("msgForm")?.apply {
                        chat.startSocket(username!!) { send ->
                            onSubmit {
                                (document.getElementById("msg") as HTMLInputElement).apply {
                                    val msg = value
                                    send(msg)
                                    value = ""
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ChatContent.append() = when (this) {
    is Message -> "[${timestampFormatted}] $user: $content"
    is Notification -> "[${timestampFormatted}] $content"
}.append()

private fun String.append() {
    document.getElementById("textArea")!!.apply {
        innerHTML = "${this@append}\n$innerHTML"
    }
}

private fun Element.onSubmit(block: () -> Unit) {
    addEventListener("submit", {
        it.stopPropagation()
        it.preventDefault()
        block()
    })
}

private fun HTMLElement.chat() = append {
    form {
        id = "msgForm"

        textInput {
            id = "msg"
            autoComplete = false
        }
        submitInput {
            hidden = true
        }
    }
    textArea {
        id = "textArea"
        readonly = true
    }
}