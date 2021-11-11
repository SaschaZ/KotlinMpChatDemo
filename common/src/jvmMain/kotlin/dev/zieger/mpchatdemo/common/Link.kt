package dev.zieger.mpchatdemo.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi


@ExperimentalComposeWebWidgetsApi
@Composable
actual fun Link(url: String, content: String) {
    val urlToOpen = remember { mutableStateOf<String?>(null) }
    Text(content,
        modifier = Modifier
            .clickable { urlToOpen.value = url }
            .padding(8.dp),
        textDecoration = TextDecoration.Underline)

    urlToOpen.value?.also {
        LocalUriHandler.current.openUri(it)
        urlToOpen.value = null
    }
}