package dev.zieger.mpchatdemo.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction


@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun TextField(
    content: State<String>,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    button: @Composable () -> Unit,
    maxLines: Int?,
    focusRequester: (() -> Unit) -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material.TextField(
            content.value, {
                if (it.last() == '\n') onSubmit()
                else onValueChange(it)
            },
            label = { label() },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            singleLine = maxLines == 1,
            maxLines = maxLines ?: Int.MAX_VALUE,
            modifier = Modifier.focusRequester(FocusRequester().also { req ->
                focusRequester { req.requestFocus() }
            }).onKeyEvent {
                if (it.key == Key.Enter) {
                    onSubmit()
                    true
                } else false
            }
        )
        button()
    }
}