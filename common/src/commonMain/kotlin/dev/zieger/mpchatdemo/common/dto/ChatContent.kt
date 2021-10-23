package dev.zieger.mpchatdemo.common.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatContent {

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
        override val type = ChatContentType.NOTIFICATION
    }

    @Serializable
    data class Message(
        override val user: ChatUser,
        override val key: Long,
        override val timestampFormatted: String,
        override val content: String
    ) : ChatContent() {
        override val type = ChatContentType.MESSAGE
    }
}

@Serializable
enum class ChatContentType { NOTIFICATION, MESSAGE }

