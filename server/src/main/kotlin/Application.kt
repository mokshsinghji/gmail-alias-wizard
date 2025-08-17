package com.moksh

import com.moksh.config.*
import io.ktor.server.application.*
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    routing {
        singlePageApplication {
            useResources = true
            filesPath = "www"
            defaultPage = "_shell.html"
        }
    }

    configureSecurity()
    configureSerialization()
    configureFrameworks()
    configureRouting()
}
