package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State


@Composable
expect fun TextField(
    content: State<String>,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit = {},
    button: @Composable () -> Unit = {},
    maxLines: Int? = null,
    focusRequester: (() -> Unit) -> Unit = {},
    onSubmit: () -> Unit = {}
)