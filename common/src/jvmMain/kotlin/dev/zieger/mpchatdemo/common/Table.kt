package dev.zieger.mpchatdemo.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
actual fun Table(block: @Composable TableScope.() -> Unit) {
    TableScope().block()
}

actual class TableScope : ITableScope {

    @Composable
    override fun Column(block: @Composable () -> Unit) =
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = androidx.compose.ui.Alignment.Start
        ) { block() }

    @OptIn(ExperimentalComposeWebWidgetsApi::class)
    @Composable
    override fun Row(block: @Composable () -> Unit) =
        Row(
            horizontalArrangement = org.jetbrains.compose.common.foundation.layout.Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) { block() }
}