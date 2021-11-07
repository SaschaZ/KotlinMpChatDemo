package dev.zieger.mpchatdemo.server.db

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.server.ChatMessagePipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class DbMessageBridge(
    scope: CoroutineScope,
    input: ReceiveChannel<ChatContent>
) : ChatMessagePipe(scope, input) {

    init {
        scope.launch {
            ChatContents.all().forEach {
                output.send(it)
            }
        }
    }

    override suspend fun onNewMessage(content: ChatContent) {
        ChatContents.add(content)
        output.send(content.copy(stored = true))
    }
}