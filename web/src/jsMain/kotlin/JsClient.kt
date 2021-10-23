import dev.zieger.mpchatdemo.common.Chat
import io.ktor.http.*
import kotlinx.browser.document
import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.renderComposable

@InternalCoroutinesApi
@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
//    val docUrl = Url(document.URL)
//    val port = 9020
//    val path = "/"
//    val url = Url(
//        URLProtocol.HTTP, docUrl.host, port, docUrl.encodedPath,
//        Parameters.Empty, "", null, null, false
//    )

    renderComposable(rootElementId = "root") {
        Chat(Url(document.URL))
    }
}