package dev.zieger.mpchatdemo.server.db

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File


val db by lazy {
    Database.connect(
        "jdbc:sqlite:${
            File("./data.db")
                .absolutePath
        }", "org.sqlite.JDBC"
    ).also {
        TransactionManager.defaultDatabase = it
        createTables()
    }
}

private val TABLES = arrayOf(Users, ChatContents)

private fun createTables(drop: Boolean = false) {
    transaction {
        if (drop)
            SchemaUtils.drop(*TABLES)
        SchemaUtils.create(*TABLES)
    }
}

fun dbMessageBridge(
    scope: CoroutineScope,
    msgChannel: ReceiveChannel<ChatContent>
): SharedFlow<ChatContent> {
    val outChannel = Channel<ChatContent>()
    scope.launch {
        ChatContents.all().forEach { outChannel.send(it) }

        for (content in msgChannel) {
            ChatContents.add(content)
            outChannel.send(content)
        }
    }
    return flow { emitAll(outChannel) }
        .shareIn(scope, SharingStarted.Eagerly, 128)
}
