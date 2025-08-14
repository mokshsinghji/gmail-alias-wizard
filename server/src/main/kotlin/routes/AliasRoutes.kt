package com.moksh.routes

import com.moksh.config.UserSession
import com.moksh.services.AuthService
import com.moksh.services.GoogleService
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.serialization.Serializable

fun Route.getCurrentAliasesApiRoute() {
    get("") {
        val authService: AuthService by call.application.dependencies
        val googleService: GoogleService by call.application.dependencies

        val userId = call.sessions.get<UserSession>()?.userId ?: return@get call.respondText("No user logged in")
        val user = authService.getUserById(userId) ?: return@get call.respondText(
            "User not found",
            status = HttpStatusCode.NotFound
        )

        val aliases = googleService.getGmailAliases(user)
        if (aliases.isNullOrEmpty()) {
            return@get call.respondText("No aliases found for user", status = HttpStatusCode.NotFound)
        }

        call.respond(HttpStatusCode.OK, aliases)
    }
}

@Serializable
data class GmailAliasRequest(
    val emailAlias: String,
    val displayName: String
)

fun Route.postNewAliasApiRoute() {
    post("") {
        val authService: AuthService by call.application.dependencies
        val googleService: GoogleService by call.application.dependencies

        val userId = call.sessions.get<UserSession>()?.userId ?: return@post call.respondText("No user logged in")
        val user = authService.getUserById(userId) ?: return@post call.respondText(
            "User not found",
            status = HttpStatusCode.NotFound
        )

        // Assuming the email alias is sent in the request body
        val emailAlias = try {
            call.receive<GmailAliasRequest>()
        } catch (e: Exception) {
            return@post call.respondText("Invalid request format", status = HttpStatusCode.BadRequest)
        }


        val newAlias = try {
            googleService.createGmailAlias(
                user, emailAlias.displayName, emailAlias.emailAlias
            )
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
            // Handle specific exceptions if needed
            return@post call.respondText("Failed to create alias: ${e.message}", status = HttpStatusCode.InternalServerError)
        }
        if (newAlias == null) {
            return@post call.respondText("Failed to create alias", status = HttpStatusCode.InternalServerError)
        }

        call.respond(HttpStatusCode.Created, newAlias)
    }
}

fun Route.registerAliasRoutes() {
    route("/api/aliases") {
        getCurrentAliasesApiRoute()
        postNewAliasApiRoute()
    }
}