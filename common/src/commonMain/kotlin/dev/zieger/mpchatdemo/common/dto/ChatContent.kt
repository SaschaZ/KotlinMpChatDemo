package dev.zieger.mpchatdemo.common.dto

import kotlinx.serialization.Serializable
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi

@Serializable
enum class ChatContentType { NOTIFICATION, MESSAGE }

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

    @OptIn(ExperimentalComposeWebWidgetsApi::class)
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
data class ChatUser(
    val id: Long,
    val name: String,
    val colorArgb: String
) {
    @OptIn(ExperimentalComposeWebWidgetsApi::class)
    val color: Color
        get() = colorArgb.toColor()
}

