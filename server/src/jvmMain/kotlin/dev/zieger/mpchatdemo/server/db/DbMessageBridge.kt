package dev.zieger.mpchatdemo.server.db

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.server.ChatMessageBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class DbMessageBridge(
    override val scope: CoroutineScope,
    override val input: ReceiveChannel<ChatContent>,
    override val output: Channel<ChatContent> = Channel()
) : ChatMessageBridge, ReceiveChannel<ChatContent> by output {

    init {
        scope.launch {
            ChatContents.all().forEach { output.send(it) }

            for (content in input) {
                ChatContents.add(content)
                output.send(content.copy(stored = true))
            }
        }
    }
}