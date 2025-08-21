val exposed_version: String by project
val h2_version: String by project
val kotlin_version: String by project
val kotlinx_browser_version: String by project
val kotlinx_html_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.2.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

tasks.withType<ProcessResources> {
    val wasmOutput = file("../web/build/dist/wasmJs/productionExecutable")
    if (wasmOutput.exists()) {
        inputs.dir(wasmOutput)
    }

    from("../web/build/dist/wasmJs/productionExecutable") {
        into("web")
        include("**/*")
    }
    duplicatesStrategy = DuplicatesStrategy.WARN
}

dependencies {
    // for gmail api
    implementation("com.google.api-client:google-api-client:2.8.0")
    implementation("com.google.auth:google-auth-library-bom:1.30.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.google.apis:google-api-services-gmail:v1-rev20230925-2.0.0")

    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-apache")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinx_html_version")
    implementation("io.ktor:ktor-server-htmx")
    implementation("io.ktor:ktor-htmx-html")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:${exposed_version}")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    implementation("io.ktor:ktor-server-di")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-client-cio-jvm:3.2.3")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}


jlink {
    // ... your other jlink options

    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages", "--exclude-module", "java.rmi"))
}

// Or if you are configuring jpackage directly
jpackage {
    // ... your other jpackage options

    jlinkOptions.set(listOf("--exclude-module", "java.rmi"))
}