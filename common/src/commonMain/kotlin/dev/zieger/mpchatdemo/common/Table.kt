@file:Suppress("FunctionName")

package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier

@ExperimentalComposeWebWidgetsApi
@Composable
expect fun Table(modifier: Modifier, block: @Composable TableScope.() -> Unit)

expect class TableScope {

    @Composable
    fun Tr(block: @Composable (TableScope.() -> Unit))

    @Composable
    fun Td(block: @Composable (TableScope.() -> Unit))
}