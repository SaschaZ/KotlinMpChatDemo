package dev.zieger.mpchatdemo.common.chat.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatUser(
    val id: Long,
    val name: String,
    val color: Color
)