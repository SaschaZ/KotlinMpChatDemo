package dev.zieger.mpchatdemo.common.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatContent {

    abstract val type: String
    abstract val key: Long
    abstract val timestampFormatted: String
    abstract val content: String

    @Serializable
    data class Notification(
        override val key: Long,
        override val timestampFormatted: String,
        override val content: String
    ) : ChatContent() {
        override val type = "Notification"
    }

    @Serializable
    data class Message(
        val user: String,
        override val key: Long,
        override val timestampFormatted: String,
        override val content: String
    ) : ChatContent() {
        override val type = "Message"
    }
}