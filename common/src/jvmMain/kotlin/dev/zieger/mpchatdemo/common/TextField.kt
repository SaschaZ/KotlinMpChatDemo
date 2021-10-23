package dev.zieger.mpchatdemo.common

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction


@OptIn(ExperimentalComposeUiApi::class)
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
    androidx.compose.material.TextField(
        content.value, { onValueChange(it) },
        label = { label() },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        singleLine = singleLine,
        maxLines = maxLines ?: Int.MAX_VALUE,
        modifier = androidx.compose.ui.Modifier.focusRequester(FocusRequester().also { req ->
            focusRequester { req.requestFocus() }
        }).onKeyEvent {
            if (it.key == Key.Enter) {
                onSubmit()
                true
            } else false

        }
    )
}