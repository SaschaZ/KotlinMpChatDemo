package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType.*
import dev.zieger.mpchatdemo.common.chat.dto.ChatUser
import dev.zieger.mpchatdemo.common.chat.dto.Color
import dev.zieger.mpchatdemo.common.chat.dto.fromHexChar
import dev.zieger.mpchatdemo.server.db.Users
import dev.zieger.utils.time.ITimeStamp
import dev.zieger.utils.time.TimeFormat
import dev.zieger.utils.time.TimeStamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class ChatBot(
    scope: CoroutineScope,
    input: ReceiveChannel<ChatContent>
) : ChatMessagePipe(scope, input) {

    override suspend fun onNewMessage(content: ChatContent) {
        if (!content.content.startsWith("/")) {
            output.send(content)
            return
        }

        println("on new command: $content")

        handleColorChange(content)
            ?: handleMe(content)
            ?: run {
                output.sendNotification(
                    content.user,
                    "invalid command: ${content.content}"
                )
            }
    }

    private suspend fun handleColorChange(
        c: ChatContent
    ) = """/color #([\dA-Fa-f]{2})([\dA-Fa-f]{2})([\dA-Fa-f]{2})""".toRegex()
        .find(c.content)?.groupValues?.let { match ->
            if (match.size == 4) {
                Color(
                    match.getOrNull(1)?.toList()?.fromHexChar() ?: 0,
                    match.getOrNull(2)?.toList()?.fromHexChar() ?: 0,
                    match.getOrNull(3)?.toList()?.fromHexChar() ?: 0
                )
            } else null
        }?.let { color ->
            Users.setColorForUser(c.user, color).let { user ->
                output.sendNotification(
                    user ?: c.user, when (user?.color == color) {
                        true -> "changed color to ${color.argb}"
                        false -> "change of color to ${color.argb} failed (is ${user?.color?.argb})"
                    }
                )
            }
        }

    private suspend fun handleMe(
        c: ChatContent
    ): Any? = """/me (.*)""".toRegex().find(c.content)?.groupValues?.getOrNull(1)?.also { msg ->
        println("/me $msg")
        output.sendMe(c.user, msg)
    }
}

suspend fun SendChannel<ChatContent>.sendNotification(user: ChatUser, content: String) {
    val key = TimeStamp()
    send(ChatContent(NOTIFICATION, user, key.millisLong, key.format(), content))
}

suspend fun SendChannel<ChatContent>.sendMe(user: ChatUser, content: String) {
    val key = TimeStamp()
    send(ChatContent(ME, user, key.millisLong, key.format(), content))
}

suspend fun SendChannel<ChatContent>.sendMessage(user: ChatUser, msg: String) {
    val key = TimeStamp()
    send(ChatContent(MESSAGE, user, key.millisLong, key.format(), msg))
}

private fun ITimeStamp.format(): String = formatTime(TimeFormat.CUSTOM("HH:mm:ss"))