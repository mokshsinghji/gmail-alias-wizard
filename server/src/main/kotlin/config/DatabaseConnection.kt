package com.moksh.config

import com.moksh.data.Users
import io.ktor.server.plugins.di.annotations.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("UNUSED") // used for dependency injection
fun provideDatabaseConnection(@Property("database.url") databaseUrl: String): Database {
    val db = Database.connect(databaseUrl)

    transaction(db) {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users)
    }

    return db
}