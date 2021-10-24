package dev.zieger.mpchatdemo.common.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import org.jetbrains.compose.common.foundation.layout.Box
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.TextUnit


@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
fun ChatMessageList(
    messages: SnapshotStateList<ChatContent>,
    fontSize: TextUnit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        messages.forEach { it.compose(fontSize) }
    }
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable
private fun ChatContent.compose(fontSize: TextUnit) {
    Box {
        Row {
            Text("[${timestampFormatted}] ", size = fontSize)
            Text("\"${user.name}\" ", size = fontSize, color = user.color.color)
            Text(content, size = fontSize)
        }
    }
}