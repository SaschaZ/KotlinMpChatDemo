package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable

@Composable
expect fun Table(block: @Composable TableScope.() -> Unit)

expect class TableScope : ITableScope

interface ITableScope {

    @Composable
    fun Column(block: @Composable () -> Unit)

    @Composable
    fun Row(block: @Composable () -> Unit)
}