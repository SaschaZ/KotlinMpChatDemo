package dev.zieger.mpchatdemo.common.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType
import org.jetbrains.compose.common.foundation.layout.*
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.Alignment
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.TextUnit


@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun ChatMessageList(
    messages: SnapshotStateList<ChatContent>,
    fontSize: TextUnit,
    timeFontSize: TextUnit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        messages.forEach { it.compose(fontSize, timeFontSize) }
    }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
private fun ChatContent.compose(
    fontSize: TextUnit,
    timeFontSize: TextUnit
) {
    Box {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("[${timestampFormatted}] ", size = timeFontSize)
            when (type) {
                ChatContentType.NOTIFICATION -> {
                    Text("\"${user.name}\" ", size = fontSize, color = user.color.color)
                    Text(content, size = fontSize)
                }
                ChatContentType.MESSAGE -> {
                    Text(user.name, size = fontSize, color = user.color.color)
                    Text(": $content", size = fontSize)
                }
                ChatContentType.ME -> {
                    Text("${user.name} ", size = fontSize, color = user.color.color)
                    Text(content, size = fontSize)
                }
            }
        }
    }
}