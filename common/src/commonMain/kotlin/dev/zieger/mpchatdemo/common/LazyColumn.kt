package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.*
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.unit.IntSize


@Composable
expect fun LazyColumn2(
    content: LazyListScope2.() -> Unit
)

interface LazyListScope2 {
    fun item(key: Long, content: @Composable () -> Unit)
}