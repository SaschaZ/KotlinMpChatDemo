package dev.zieger.mpchatdemo.common.chat.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatContent(
    val type: ChatContentType,
    val user: ChatUser,
    val key: Long,
    val timestampFormatted: String,
    val content: String
)

@Serializable
enum class ChatContentType { NOTIFICATION, MESSAGE }

