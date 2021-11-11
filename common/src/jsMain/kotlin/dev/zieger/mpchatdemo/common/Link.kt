package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

@ExperimentalComposeWebWidgetsApi
@Composable
actual fun Link(url: String, content: String) {
    A(url) { Text(content) }
}