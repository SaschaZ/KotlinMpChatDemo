package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType.MESSAGE
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType.NOTIFICATION
import dev.zieger.mpchatdemo.common.chat.dto.ChatUser
import dev.zieger.mpchatdemo.common.chat.dto.Color
import dev.zieger.mpchatdemo.common.chat.dto.fromHexChar
import dev.zieger.mpchatdemo.server.db.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatBot(
    scope: CoroutineScope,
    private val input: Flow<ChatContent>,
    private val output: Channel<ChatContent>
) {
    init {
        scope.launch {
            input.collect { content ->
                onNewMessage(content)
            }
        }
    }

    private suspend fun onNewMessage(content: ChatContent) {
        val c = content.content
        if (content.stored || !c.startsWith("/")) return

        """/color #([\dA-Fa-f]{2})([\dA-Fa-f]{2})([\dA-Fa-f]{2})""".toRegex()
            .find(c)?.groupValues?.let { match ->
                if (match.size == 4) {
                    Color(
                        match.getOrNull(1)?.toList()?.fromHexChar() ?: 0,
                        match.getOrNull(2)?.toList()?.fromHexChar() ?: 0,
                        match.getOrNull(3)?.toList()?.fromHexChar() ?: 0
                    )
                } else null
            }?.let { color ->
                Users.setColorForUser(content.user, color).let { user ->
                    output.sendNotification(
                        user ?: content.user, when (user?.color == color) {
                            true -> "changed color to ${color.argb}"
                            false -> "change of color to ${color.argb} failed (is ${user?.color?.argb})"
                        }
                    )
                }
            } ?: run {
            output.sendNotification(
                content.user,
                "invalid command: ${content.content}"
            )
        }
    }
}

suspend fun SendChannel<ChatContent>.sendNotification(user: ChatUser, content: String) {
    val key = System.currentTimeMillis()
    send(ChatContent(NOTIFICATION, user, key, key.format(), content))
}

suspend fun SendChannel<ChatContent>.sendMessage(user: ChatUser, msg: String) {
    val key = System.currentTimeMillis()
    send(ChatContent(MESSAGE, user, key, key.format(), msg))
}

private fun Long.format(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(this))