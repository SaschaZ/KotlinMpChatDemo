package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier

@ExperimentalComposeWebWidgetsApi
@Composable
actual fun Table(modifier: Modifier, block: @Composable TableScope.() -> Unit) {
    TableScope().block()
}

actual class TableScope {
    @ExperimentalComposeWebWidgetsApi
    @Composable
    actual fun Tr(block: @Composable TableScope.() -> Unit) {
        Row { block() }
    }

    @ExperimentalComposeWebWidgetsApi
    @Composable
    actual fun Td(block: @Composable TableScope.() -> Unit) {
        Column { block() }
    }
}