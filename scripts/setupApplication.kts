import java.security.SecureRandom
import kotlin.system.exitProcess

println("Setting up the application...")

println("Enter the port number for the application (default 8080): ")
val port = readln().toIntOrNull() ?: 8080

println("Please go to the following URL: https://console.cloud.google.com/apis/credentials")
println("And create a new OAuth 2.0 Client ID for your application.")
print("What is the Google Client ID? ")
val clientId = readln()

println("What is the Google Client Secret? ")
val clientSecret = readln()

println("Make sure to set the redirect URI to: http://localhost:$port/auth/google/callback")
println("Then, go to 'Enabled APIs & services' and enable the Gmail API.")

println("Creating configuration file...")


fun generateHexKey(length: Int): String {
    val secureRandom = SecureRandom()

    val bytes = ByteArray(length)
    secureRandom.nextBytes(bytes)

    return bytes.joinToString("") { "%02x".format(it) }
}

val encryptKey = generateHexKey(16)
val signKey = generateHexKey(32)
val configFile = "../server/src/main/resources/application.yaml"

val configFileContent = """
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
        encryptKey: $encryptKey
        signKey: $signKey
      google:
        clientId: $clientId
        clientSecret: $clientSecret
""".trimIndent()

val file = java.io.File(configFile)
file.parentFile.mkdirs()
file.writeText(configFileContent)
println("Configuration file created at $configFile")

// build react app and copy to server resources
println("Building the web application...")
println("Please ensure you have Node.js and npm installed.")
val webBuildCommand = "cd ../react-frontend && npm install && npm run build"
println("Running the build command: $webBuildCommand")

val process = ProcessBuilder(*webBuildCommand.split(" ").toTypedArray())
    .directory(java.io.File("../web"))
    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
    .redirectErrorStream(true)
    .start()

val exitCode = process.waitFor()
if (exitCode != 0) {
    println("Failed to build the web application. Please check the output above for errors.")
    exitProcess(1)
}

println("Web application built successfully.")
println("Copying built files to server resources...")
val reactFrontendBuild = java.io.File("../react-frontend/.output/public")
val serverResourcesDir = java.io.File("../server/src/main/resources/www")
if (serverResourcesDir.exists()) {
    serverResourcesDir.deleteRecursively()
}

reactFrontendBuild.copyRecursively(serverResourcesDir, overwrite = true)
println("Built files copied to $serverResourcesDir")

println("Setup complete! You can now run the server using the command:")
println("cd ../server && ./gradlew run")

println("Would you like to run the server now? (yes/no)")
val runServer = readln().trim().lowercase() == "yes"
if (runServer) {
    println("Running the server...")
    val runCommand = "./gradlew run"
    val runProcess = ProcessBuilder(*runCommand.split(" ").toTypedArray())
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectErrorStream(true)
        .start()

    val runExitCode = runProcess.waitFor()
    if (runExitCode != 0) {
        println("Failed to start the server. Please check the output above for errors.")
        exitProcess(1)
    } else {
        println("Server is running! Visit http://localhost:$port in your browser.")
    }
} else {
    println("You can run the server later using the command: ./gradlew run")
}