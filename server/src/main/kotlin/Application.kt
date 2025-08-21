package com.moksh

import com.moksh.config.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.config.*
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.routing.routing
import java.io.File

fun main(args: Array<String>) {
    // Check if we need to run setup first
    if (args.contains("--setup")) {
        runSetup()
        return
    }
    
    // If external config exists, start the server with it
    val externalConfigPath = System.getProperty("config.file") ?: "./application.yaml"
    if (File(externalConfigPath).exists()) {
        // Set system properties for Ktor to use external config
        System.setProperty("config.file", externalConfigPath)
        System.setProperty("config.resource", "")
    }
    
    // Filter out --setup from args when passing to EngineMain
    val filteredArgs = args.filter { it != "--setup" }.toTypedArray()
    io.ktor.server.netty.EngineMain.main(filteredArgs)
}

fun runSetup() {
    println("Gmail Alias Wizard - Configuration Setup")
    println("=======================================")
    
    val configContent = """
ktor:
  application:
    modules:
      - com.moksh.ApplicationKt.module
    dependencies:
      - com.moksh.config.DatabaseConnectionKt.provideDatabaseConnection
      - com.moksh.services.AuthServiceImpl
      - com.moksh.services.GoogleServiceImpl
  deployment:
    port: 8080

database:
  url: jdbc:sqlite:./data.db # can be changed to any other database

auth:
  sessions:
    encryptKey: <CREATE_WITH_OPENSSL>
    signKey: <CREATE_WITH_OPENSSL>
  google:
    clientId: <YOUR_GOOGLE_CLIENT_ID>
    clientSecret: <YOUR_GOOGLE_CLIENT_SECRET>
    appPassword: <YOUR_GMAIL_APP_PASSWORD>
    smtpUsername: <YOUR_GMAIL_ADDRESS>
""".trimIndent()

    val configFile = File("application.yaml")
    if (configFile.exists()) {
        print("Configuration file already exists. Overwrite? (y/N): ")
        val response = readLine()?.lowercase()
        if (response != "y" && response != "yes") {
            println("Setup cancelled.")
            return
        }
    }
    
    configFile.writeText(configContent)
    println("\nConfiguration template created: application.yaml")
    println("\nNext steps:")
    println("1. Generate encryption keys with OpenSSL:")
    println("   openssl rand -hex 16  # for encryptKey")
    println("   openssl rand -hex 16  # for signKey")
    println("2. Configure your Google OAuth credentials")
    println("3. Update database URL if needed")
    println("4. Run the application: ./gmail-alias-wizard")
    println("\nDone!")
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
