import dev.zieger.mpchatdemo.common.chat.Chat
import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.renderComposable

@InternalCoroutinesApi
@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    renderComposable(rootElementId = "root") {
        Chat()
    }
}