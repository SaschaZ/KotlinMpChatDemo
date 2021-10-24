package dev.zieger.mpchatdemo.server.db

import dev.zieger.mpchatdemo.common.chat.dto.ChatContent
import dev.zieger.mpchatdemo.common.chat.dto.ChatContentType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Table definition for storing all messages and notifications.
 */
object ChatContents : LongIdTable() {
    val type = varchar("type", 32)
    val user = reference("user", Users)
    val key = long("key")
    val timestampFormatted = varchar("timestamp", 128)
    val content = varchar("content", 1024)

    fun add(cc: ChatContent) {
        transaction(db) {
            insert {
                it[type] = cc.type.name
                it[user] = cc.user.id
                it[key] = cc.key
                it[timestampFormatted] = cc.timestampFormatted
                it[content] = cc.content
            }
        }
    }

    fun all(): List<ChatContent> = transaction(db) {
        ChatContentEntry.all().toList().map { it.chatContent }
    }
}

/**
 * Represents a single entry of the ChatContents table.
 */
class ChatContentEntry(entityID: EntityID<Long>) : LongEntity(entityID) {
    companion object : LongEntityClass<ChatContentEntry>(ChatContents)

    var type by ChatContents.type
    var user: UserEntry by UserEntry referencedOn ChatContents.user
    var key by ChatContents.key
    var timestampFormatted by ChatContents.timestampFormatted
    var content by ChatContents.content

    /**
     * Helper method to convert a ChatContentEntry to a ChatContent instance.
     */
    val chatContent: ChatContent
        get() = ChatContent(
            ChatContentType.valueOf(type), user.chatUser, key, timestampFormatted, content
        )
}
