package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.dto.ChatContent
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File


val db by lazy {
    Database.connect("jdbc:sqlite:${File("./data.db").absolutePath}", "org.sqlite.JDBC").also {
        TransactionManager.defaultDatabase = it
        it.createTables()
    }
}

private val TABLES = arrayOf(ChatContents)

fun Database.createTables(drop: Boolean = false) {
    transaction {
        if (drop)
            SchemaUtils.drop(*TABLES)
        SchemaUtils.create(*TABLES)
    }
}

object ChatContents : LongIdTable() {
    val type = varchar("type", 32)
    val user = varchar("user", 128)
    val key = long("key")
    val timestamp = varchar("timestamp", 128)
    val content = varchar("content", 1024)

    fun add(cc: ChatContent) {
        transaction(db) {
            insert {
                it[type] = cc.type
                it[user] = (cc as? ChatContent.Message)?.user ?: ""
                it[key] = cc.key
                it[timestamp] = cc.timestampFormatted
                it[content] = cc.content
            }
        }
    }

    fun all(): List<ChatContent> = transaction(db) {
        selectAll().map {
            when (it.getOrNull(type)) {
                "Notification" -> ChatContent.Notification(
                    it.getOrNull(key)!!,
                    it.getOrNull(timestamp)!!,
                    it.getOrNull(content)!!
                )
                "Message" -> ChatContent.Message(
                    it.getOrNull(user)!!,
                    it.getOrNull(key)!!,
                    it.getOrNull(timestamp)!!,
                    it.getOrNull(content)!!
                )
                else -> throw IllegalStateException("Unknown type ${it.getOrNull(type)}")
            }
        }
    }
}