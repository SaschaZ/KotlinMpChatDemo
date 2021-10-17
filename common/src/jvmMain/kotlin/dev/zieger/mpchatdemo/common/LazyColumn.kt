package dev.zieger.mpchatdemo.common

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.foundation.layout.width
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.size
import org.jetbrains.compose.common.ui.unit.IntSize
import org.jetbrains.compose.common.ui.unit.dp

@ExperimentalComposeWebWidgetsApi
@Composable
actual fun LazyColumn2(
    content: LazyListScope2.() -> Unit
) {
    LazyColumn {
        object : LazyListScope2 {
            override fun item(key: Long, content: @Composable () -> Unit) {
                this@LazyColumn.item(key) {
                    content()
                }
            }
        }.content()
    }
}