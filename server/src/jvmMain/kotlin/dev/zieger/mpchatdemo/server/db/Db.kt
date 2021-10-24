package dev.zieger.mpchatdemo.server.db

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
