package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel

class Emoticons(
    scope: CoroutineScope,
    input: ReceiveChannel<ChatContent>
) : ChatMessagePipe(scope, input) {

    companion object {

        enum class Emoti(val raw: Pair<List<String>, String>) {
            SMILE(listOf(":)", ":-)") to "\uD83D\uDE00"),
            GREAT_SMILE(listOf(":D", ":-D") to "😁"),
            WINKING(listOf(";)", ";-)") to "\uD83D\uDE09"),
            UPSIDE_DOWN(listOf("(:", "(-:") to "\uD83D\uDE43"),
            KISS(listOf(":*", ":-*") to "\uD83D\uDE18"),
            HEART(listOf("<3") to "\uD83E\uDD70"),
            TONGUE(listOf(":P", ":-P") to "\uD83D\uDE1B"),
            EXPRESSION_LESS(listOf("-.-") to "\uD83D\uDE11"),
            ROLLING_EYES(listOf("^.^") to "\uD83D\uDE44"),
            CRYING(listOf(":'(") to "\uD83D\uDE25"),
            SAD(listOf(":(", ":-(") to "☹")
        }
    }

    override suspend fun onNewMessage(content: ChatContent) {
        output.send(content.copy(content = Emoti.values().fold(content.content) { c, e ->
            e.raw.first.fold(c) { c0, e0 -> c0.replace(e0, e.raw.second) }
        }))
    }
}