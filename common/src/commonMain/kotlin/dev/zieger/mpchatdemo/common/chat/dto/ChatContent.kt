package dev.zieger.mpchatdemo.common.chat.dto

import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType.MESSAGE
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType.NOTIFICATION
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatContent {

    companion object {

        operator fun invoke(
            type: ChatContentType,
            user: ChatUser,
            key: Long,
            timestampFormatted: String,
            content: String
        ): ChatContent = when (type) {
            NOTIFICATION -> Notification(user, key, timestampFormatted, content)
            MESSAGE -> Message(user, key, timestampFormatted, content)
        }
    }

    abstract val user: ChatUser
    abstract val type: ChatContentType
    abstract val key: Long
    abstract val timestampFormatted: String
    abstract val content: String

    @Serializable
    data class Notification(
        override val user: ChatUser,
        override val key: Long,
        override val timestampFormatted: String,
        override val content: String
    ) : ChatContent() {
        override val type = NOTIFICATION
    }

    @Serializable
    data class Message(
        override val user: ChatUser,
        override val key: Long,
        override val timestampFormatted: String,
        override val content: String
    ) : ChatContent() {
        override val type = MESSAGE
    }
}

@Serializable
enum class ChatContentType { NOTIFICATION, MESSAGE }

