package com.moksh.services

import com.moksh.data.User
import com.moksh.data.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
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
class AuthServiceImpl(private val database: Database) : AuthService {
    override fun upsertGoogleUser(user: GoogleUser): User {
        return transaction(database) {

            if (User.find { Users.googleUserId eq user.id }.count() > 0) {
                // If the user already exists, update their information
                User.find { Users.googleUserId eq user.id }.first().apply {
                    name = user.name
                    email = user.email
                    picture = user.picture
                    googleAccessToken = user.accessToken
                    if (!user.refreshToken.isNullOrEmpty())
                        googleRefreshToken = user.refreshToken
                }

                return@transaction User.find { Users.googleUserId eq user.id }.first()
            } else {
                return@transaction User.new {
                    name = user.name
                    email = user.email
                    picture = user.picture
                    googleUserId = user.id
                    googleAccessToken = user.accessToken
                    googleRefreshToken = user.refreshToken ?: ""
                }
            }
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