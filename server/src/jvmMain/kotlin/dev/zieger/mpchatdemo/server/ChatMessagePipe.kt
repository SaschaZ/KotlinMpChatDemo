package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

abstract class ChatMessagePipe(
    scope: CoroutineScope,
    private val input: ReceiveChannel<ChatContent>,
    output: Channel<ChatContent> = Channel()
) : ReceiveChannel<ChatContent> by output {

    protected val output: SendChannel<ChatContent> = output

    init {
        scope.launch {
            for (content in input)
                onNewMessage(content)
        }
    }

    abstract suspend fun onNewMessage(content: ChatContent)
}