package com.moksh.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users") {
    val name = text("name")
    val email = text("email").uniqueIndex()
    val picture = text("picture").nullable() // Optional field for user profile picture
    val googleUserId = text("google_user_id").uniqueIndex()
    val googleAccessToken = text("google_access_token")
    val googleRefreshToken = text("google_refresh_token")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var picture by Users.picture
    var googleUserId by Users.googleUserId
    var googleAccessToken by Users.googleAccessToken
    var googleRefreshToken by Users.googleRefreshToken
}