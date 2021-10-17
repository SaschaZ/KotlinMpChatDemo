package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier


@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
expect fun TextField(
    content: MutableState<String>,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit = {},
    singleLine: Boolean = false,
    maxLines: Int? = if (singleLine) 1 else null,
    focusRequester: (() -> Unit) -> Unit = {},
    onSubmit: () -> Unit = {}
)