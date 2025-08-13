package com.moksh.services

import com.moksh.data.User
import com.moksh.data.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

data class GoogleUser(
    val id: String,
    val name: String,
    val email: String,
    val picture: String? = null,
    val accessToken: String,
    val refreshToken: String? = null
)

interface AuthService {
    fun upsertGoogleUser(user: GoogleUser): User
    fun getUserByGoogleId(googleUserId: String): User?
    fun getUserById(id: Int): User?
}

@Suppress("UNUSED") // used for dependency injection
class AuthServiceImpl(private val database: Database): AuthService {
    override fun upsertGoogleUser(user: GoogleUser): User {
        return transaction(database) {
            val result = Users.upsert(where = { Users.googleUserId eq user.id }) {
                it[googleUserId] = user.id
                it[name] = user.name
                it[email] = user.email
                it[picture] = user.picture
                it[googleAccessToken] = user.accessToken
                if (!user.refreshToken.isNullOrEmpty()) {
                    it[googleRefreshToken] = user.refreshToken
                }
            }.resultedValues!!.first()

            User.wrapRow(result)
        }
    }

    override fun getUserByGoogleId(googleUserId: String): User? {
        return transaction(database) {
            User.find { Users.googleUserId eq googleUserId }.firstOrNull()
        }
    }

    override fun getUserById(id: Int): User? {
        return transaction(database) {
            User.findById(id)
        }
    }
}