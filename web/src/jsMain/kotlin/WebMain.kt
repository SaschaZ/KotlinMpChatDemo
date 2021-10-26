import dev.zieger.mpchatdemo.common.chat.Chat
import io.ktor.http.*
import kotlinx.browser.document
import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.renderComposable

@InternalCoroutinesApi
@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    renderComposable(rootElementId = "root") {
        val url = Url(document.URL)
        val isLocal = url.host.contains("localhost")
        Chat(
            url.host, when (isLocal) {
                true -> 9024
                false -> url.port
            },
            useDarkButtonColor = true
        )
    }
}