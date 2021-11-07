package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier

@ExperimentalComposeWebWidgetsApi
@Composable
actual fun Table(modifier: Modifier, block: @Composable TableScope.() -> Unit) {
    org.jetbrains.compose.web.dom.Table {
        Row(modifier) {
            TableScope().block()
        }
    }
}

actual class TableScope {

    @Composable
    actual fun Tr(block: @Composable (TableScope.() -> Unit)) {
        org.jetbrains.compose.web.dom.Tr(null) { block() }
    }

    @Composable
    actual fun Td(block: @Composable (TableScope.() -> Unit)) {
        org.jetbrains.compose.web.dom.Td(null) { block() }
    }
}