package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Td
import org.jetbrains.compose.web.dom.Tr

@Composable
actual fun Table(block: @Composable TableScope.() -> Unit) {
    org.jetbrains.compose.web.dom.Table {
        TableScope().block()
    }
}

actual class TableScope : ITableScope {

    @Composable
    override fun Column(block: @Composable () -> Unit) = Td { block() }

    @Composable
    override fun Row(block: @Composable () -> Unit) = Tr { block() }
}