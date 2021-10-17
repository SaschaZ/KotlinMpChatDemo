package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.IntSize
import org.jetbrains.compose.web.dom.TextInput

@OptIn(ExperimentalComposeWebWidgetsApi::class)
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
    TextInput(content.value ?: "") {
        onChange {
            onValueChange(it.value)
        }
    }
}