package com.moksh.routes

import com.moksh.config.UserSession
import com.moksh.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.route
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.html.*
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val id: Int,
    val name: String,
    val email: String,
    val googleUserId: String,
    val picture: String?
)

fun Route.userInfoApiRoute() {
    get("/user/info") {
        val userSession = call.sessions.get<UserSession>()
        if (userSession == null) {
            call.respondText("No user logged in")
            return@get
        }

        val authService: AuthService by call.application.dependencies;
        val user = authService.getUserById(userSession.userId);
        if (user != null) {
            call.respond(
                HttpStatusCode.OK, UserInfoResponse(
                    id = user.id.value,
                    name = user.name,
                    email = user.email,
                    googleUserId = user.googleUserId,
                    picture = user.picture
                )
            )
        } else {
            call.respondText("User not found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.seeUserInfoPage() {
    get("/user/info") {
        val userSession = call.sessions.get<UserSession>()

        val authService: AuthService by call.application.dependencies;
        val user = userSession?.let { authService.getUserById(it.userId) };
        if (user != null) {
            call.respondHtml {
                head {
                    title {
                        +"User Information"
                    }
                }
                body {
                    h1 { +"User Information" }
                    p { +"ID: ${user.id.value}" }
                    p { +"Name: ${user.name}" }
                    p { +"Email: ${user.email}" }
                    p { +"Google User ID: ${user.googleUserId}" }
                }
            }
        } else {
            call.respondText("User not found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.registerAuthRoutes() {
    route("/api/auth/") {
        userInfoApiRoute()
    }

    route("/auth/") {
        seeUserInfoPage()
    }
}