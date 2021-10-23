package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.jetbrains.compose.web.dom.TextInput


@Composable
actual fun TextField(
    content: MutableState<String>,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    singleLine: Boolean,
    maxLines: Int?,
    focusRequester: (() -> Unit) -> Unit,
    onSubmit: () -> Unit
) {
    TextInput(content.value) {
        onInput { onValueChange(it.value) }
        onKeyDown { if (it.key == "Enter") onSubmit() }
    }
}