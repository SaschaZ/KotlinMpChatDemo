package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import org.jetbrains.compose.common.foundation.layout.Arrangement
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.dom.TextInput


@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
actual fun TextField(
    content: State<String>,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    maxLines: Int?,
    focusRequester: (() -> Unit) -> Unit, // not implemented for JS
    onSubmit: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        label()
        TextInput(content.value) {
            onInput {
                val text = it.value
                if (text.last() == '\n') onSubmit()
                else onValueChange(text)
            }
            onKeyDown { if (it.key == "Enter") onSubmit() }
        }
    }
}