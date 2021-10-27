package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

interface ChatMessageBridge : ReceiveChannel<ChatContent> {

    val scope: CoroutineScope
    val input: ReceiveChannel<ChatContent>
    val output: Channel<ChatContent>
}