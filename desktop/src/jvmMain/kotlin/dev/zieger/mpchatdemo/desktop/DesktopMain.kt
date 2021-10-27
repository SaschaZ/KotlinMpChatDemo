package dev.zieger.mpchatdemo.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import dev.zieger.mpchatdemo.common.Constants
import dev.zieger.mpchatdemo.common.chat.Chat
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    org.apache.log4j.BasicConfigurator.configure()
    singleWindowApplication(
        state = WindowState(size = DpSize(1200.dp, 800.dp)),
        title = "Kotlin Multiplatform Chat Demo"
    ) {
        val host = args.getOrNull(args.indexOf("-h") + 1) ?: Constants.EXTERNAL_HOST
        val port = args.getOrNull(args.indexOf("-p") + 1)?.toIntOrNull() ?: Constants.EXTERNAL_PORT
        Chat(host, port)
    }
}