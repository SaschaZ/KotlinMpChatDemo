package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.foundation.layout.offset
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.dp

@ExperimentalComposeWebWidgetsApi
@Composable
actual fun LazyColumn2(
    content: LazyListScope2.() -> Unit
) {
    val items = remember { mutableStateListOf<@Composable () -> Unit>() }
    val yScroll = remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxWidth()) {
        object : LazyListScope2 {
            override fun item(key: Long, content: @Composable () -> Unit) {
                items.add(content)
            }
        }.content()

        items.forEach { Box(modifier = Modifier.offset(y = yScroll.value.dp, x = 0.dp)) { it() } }
    }
}