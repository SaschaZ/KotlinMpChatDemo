package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zieger.mpchatdemo.common.dto.ChatContent
import org.jetbrains.compose.common.foundation.layout.*
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.TextUnit
import org.jetbrains.compose.common.ui.unit.dp


@ExperimentalComposeWebWidgetsApi
@Composable
fun ChatMessageList(
    messages: SnapshotStateList<ChatContent>,
    fontSize: TextUnit
) {
    val yScroll = remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxWidth()) {
        messages.forEach { msg ->
            Box(modifier = Modifier.offset(y = yScroll.value.dp, x = 0.dp)) {
                Row {
                    Text("[${msg.timestampFormatted}] ", size = fontSize)
                    Text("\"${msg.user.name}\" ", size = fontSize, color = msg.user.color.color)
                    Text(msg.content, size = fontSize)
                }
            }
        }
    }
}