package com.moksh.config

import com.moksh.services.AuthService
import com.moksh.services.GoogleUser
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.OAuthServerSettings.OAuth2ServerSettings
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.hex
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

fun Application.configureSecurity() {
    val googleAuthConfig = environment.config.config("auth.google")
    val sessionSettings = environment.config.config("auth.sessions")
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/auth/google/callback" }
            providerLookup = {
                OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = googleAuthConfig.property("clientId").getString(),
                    clientSecret = googleAuthConfig.property("clientSecret").getString(),
                    defaultScopes = listOf(
                        "profile",
                        "email",
                        "openid",
                        "https://www.googleapis.com/auth/gmail.settings.sharing",
                        "https://www.googleapis.com/auth/gmail.readonly"
                    ),
                    extraAuthParameters = listOf("access_type" to "offline", "prompt" to "consent")
                )
            }

            client = HttpClient(CIO)
        }
    }

    install(Sessions) {
        val encryptKey = hex(
            sessionSettings.propertyOrNull("encryptKey")?.getString()
                ?: throw IllegalStateException("Session encryption key not configured")
        )
        val signKey = hex(
            sessionSettings.propertyOrNull("signKey")?.getString()
                ?: throw IllegalStateException("Session signing key not configured")
        )

        cookie<UserSession>("USER_SESSION") {
            cookie.extensions["SameSite"] = "lax"
            transform(
                SessionTransportTransformerEncrypt(
                    encryptKey, signKey
                )
            )
        }
    }

    routing {
        route("/auth") {
            route("/google") {
                authenticate("auth-oauth-google") {
                    get("login") {
                        call.respondRedirect("/callback")
                    }

                    get("/callback") {
                        val authService: AuthService by call.application.dependencies
                        val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()

                        if (principal == null) {
                            call.respond(HttpStatusCode.Unauthorized, "Authentication failed")
                            return@get
                        }

                        val userInfo: GoogleUserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                            bearerAuth(principal.accessToken)
                        }.body()


                        val newUser = authService.upsertGoogleUser(GoogleUser(
                            id = userInfo.id,
                            name = userInfo.name,
                            email = userInfo.email,
                            picture = userInfo.picture ?: "",
                            accessToken = principal.accessToken,
                            refreshToken = principal.refreshToken
                        ))

                        call.sessions.set(UserSession(newUser.id.value))
                        call.respondRedirect("/hello")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class GoogleUserInfo(val id: String, val name: String, val email: String, val picture: String?)

@Serializable
class UserSession(val userId: Int)