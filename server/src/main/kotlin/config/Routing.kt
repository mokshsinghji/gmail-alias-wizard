package com.moksh.config

import com.moksh.routes.registerRoutes
import com.moksh.services.AuthService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        staticResources("/", "/web")
    }

    registerRoutes()
}
