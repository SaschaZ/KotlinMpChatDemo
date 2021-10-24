import dev.zieger.mpchatdemo.common.Chat
import io.ktor.http.*
import kotlinx.browser.document
import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.renderComposable

@InternalCoroutinesApi
@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    renderComposable(rootElementId = "root") {
        Url(document.URL).run {
            Chat(
                Url(
                    URLProtocol.HTTP, host, port, "/",
                    parameters, fragment, user, password, trailingQuery
                )
            )
        }
    }
}