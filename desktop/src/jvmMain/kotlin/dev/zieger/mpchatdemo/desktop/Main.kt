package dev.zieger.mpchatdemo.desktop

import androidx.compose.material.Text
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import dev.zieger.mpchatdemo.common.Chat
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking

@InternalCoroutinesApi
fun main(args: Array<String>) = runBlocking {
    org.apache.log4j.BasicConfigurator.configure()
    singleWindowApplication(
        state = WindowState(size = DpSize(1200.dp, 800.dp)),
        title = "Kotlin Multiplatform Chat Demo"
    ) {
        Chat()
    }
}