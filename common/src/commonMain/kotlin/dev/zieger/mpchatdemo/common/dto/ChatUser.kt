package dev.zieger.mpchatdemo.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatUser(
    val id: Long,
    val name: String,
    val color: Color
)