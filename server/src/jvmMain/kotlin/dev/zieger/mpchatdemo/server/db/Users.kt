package dev.zieger.mpchatdemo.server.db

import dev.zieger.mpchatdemo.common.chat.dto.ChatUser
import dev.zieger.mpchatdemo.common.chat.dto.Color
import dev.zieger.mpchatdemo.common.chat.dto.toColor
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Table definition for all users.
 */
object Users : LongIdTable() {
    val name = varchar("name", 32)
    val color = varchar("color", 8)

    private fun add(userName: String, colorArgb: String): ChatUser = transaction {
        insert {
            it[name] = userName
            it[color] = colorArgb
        }.let { get(userName)!! }.also { u -> println("insert new user into DB: $u") }
    }

    fun get(userName: String): ChatUser? = transaction {
        UserEntry.find { name eq userName }.toList().firstOrNull()?.let {
            it.chatUser.also { u -> println("get user from DB: $u") }
        }
    }

    fun getOrInsert(userName: String): ChatUser = get(userName) ?: add(userName, Color().argb)

    fun setColorForUser(user: ChatUser, color: Color): ChatUser? = transaction {
        UserEntry.find { name eq user.name }.toList().firstOrNull()?.let {
            it.colorArgb = color.argb
        }
    }.let { get(user.name) }
}

/**
 * Represents a single entry of the Users table.
 */
class UserEntry(entityID: EntityID<Long>) : LongEntity(entityID) {
    companion object : LongEntityClass<UserEntry>(Users)

    var name by Users.name
    var colorArgb by Users.color

    /**
     * Helper property to convert a UserEntry to a ChatUser instance.
     */
    val chatUser: ChatUser
        get() = ChatUser(id.value, name, colorArgb.toColor())
}