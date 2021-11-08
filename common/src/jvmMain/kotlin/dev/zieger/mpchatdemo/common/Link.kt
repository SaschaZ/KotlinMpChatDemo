package dev.zieger.mpchatdemo.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import java.awt.Desktop
import java.net.URI


@ExperimentalComposeWebWidgetsApi
@Composable
actual fun Link(url: String, content: String) {
    Text(content,
        modifier = androidx.compose.ui.Modifier.clickable { openWebpage(url) }
            .padding(8.dp),
        textDecoration = TextDecoration.Underline)
}

private fun openWebpage(uri: String): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(URI(uri))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}