package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.dto.ChatContent
import dev.zieger.mpchatdemo.common.dto.ChatContentType
import dev.zieger.mpchatdemo.common.dto.ChatUser
import dev.zieger.mpchatdemo.common.dto.toColor
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
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
        it.createTables()
    }
}

private val TABLES = arrayOf(Users, ChatContents)

fun Database.createTables(drop: Boolean = false) {
    transaction {
        if (drop)
            SchemaUtils.drop(*TABLES)
        SchemaUtils.create(*TABLES)
    }
}

object Users : LongIdTable() {
    val name = varchar("name", 32)
    val color = varchar("color", 8)

    fun add(userName: String, colorArgb: String): ChatUser {
        return transaction {
            insert {
                it[name] = userName
                it[color] = colorArgb
            }.let { ChatUser(it[Users.id].value, it[name], it[color].toColor()) }
        }
    }

    fun getOrInsert(userName: String): ChatUser = transaction {
        val existing = Users.select { name eq userName }.toList().firstOrNull()
            ?.let {
                ChatUser(it[Users.id].value, it[name], it[color].toColor())
                    .also { u -> println("get user from DB: $u") }
            }
        existing ?: add(userName, "0xFF0000")
            .also { u -> println("insert new user into DB: $u") }
    }

    fun all(): List<ChatUser> = UserEntry.all().toList().map { entry ->
        ChatUser(entry.id.value, entry.name, entry.colorArgb.toColor())
    }
}

class UserEntry(entityID: EntityID<Long>) : LongEntity(entityID) {
    companion object : LongEntityClass<UserEntry>(Users)

    var name by Users.name
    var colorArgb by Users.color
}

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
        ChatContentEntry.all().toList().map { entry ->
            when (ChatContentType.valueOf(entry.type)) {
                ChatContentType.NOTIFICATION -> ChatContent.Notification(
                    ChatUser(entry.user.id.value, entry.user.name, entry.user.colorArgb.toColor()),
                    entry.key, entry.timestampFormatted, entry.content
                )
                ChatContentType.MESSAGE -> ChatContent.Message(
                    ChatUser(entry.user.id.value, entry.user.name, entry.user.colorArgb.toColor()),
                    entry.key, entry.timestampFormatted, entry.content
                )
            }
        }
    }
}

class ChatContentEntry(entityID: EntityID<Long>) : LongEntity(entityID) {
    companion object : LongEntityClass<ChatContentEntry>(ChatContents)

    var type by ChatContents.type
    var user: UserEntry by UserEntry referencedOn ChatContents.user
    var key by ChatContents.key
    var timestampFormatted by ChatContents.timestampFormatted
    var content by ChatContents.content
}