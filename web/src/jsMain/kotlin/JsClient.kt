import dev.zieger.mpchatdemo.common.Chat
import io.ktor.http.*
import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.renderComposable

@InternalCoroutinesApi
@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    val port = 9021
    val path = "/"
    val url = Url(
        URLProtocol.HTTP, "localhost", port, path,
        Parameters.Empty, "", null, null, false
    )

    renderComposable(rootElementId = "root") {
        Chat(url)
    }
}