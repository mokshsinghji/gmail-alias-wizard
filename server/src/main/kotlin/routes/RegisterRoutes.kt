package com.moksh.routes

import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.registerRoutes() {
    routing {
        registerAuthRoutes()
        registerAliasRoutes()
    }
}