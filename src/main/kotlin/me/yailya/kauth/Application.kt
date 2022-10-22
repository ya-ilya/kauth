/*
 * Copyright (c) 2022. ya-ilya
 */

@file:Suppress("HttpUrlsUsage")

package me.yailya.kauth

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.yailya.kauth.config.Config
import me.yailya.kauth.database.configureDatabase
import me.yailya.kauth.plugins.configureContent
import me.yailya.kauth.plugins.configureRouting
import me.yailya.kauth.plugins.configureSecurity
import java.io.File

var HOST = "127.0.0.1"
var PORT = 8080
var JWT_SECRET = "secret"
var JWT_REALM = "Access to 'kauth'"

var JWT_AUDIENCE: String? = null
    get() = field ?: "http://$HOST:$PORT/api"
var JWT_ISSUER: String? = null
    get() = field ?: "http://$HOST:$PORT/api"

fun main() {
    val configFile = File("kauth.config.json")

    if (!configFile.exists()) {
        configFile.createNewFile()
        configFile.writeText(Json.encodeToString(Config(HOST, PORT, JWT_AUDIENCE, JWT_ISSUER, JWT_SECRET, JWT_REALM)))
    }

    val config = Json.decodeFromString<Config>(configFile.readText())

    config.host.ifNotNull { HOST = it }
    config.port.ifNotNull { PORT = it }
    config.jwtAudience.ifNotNull { JWT_AUDIENCE = it }
    config.jwtIssuer.ifNotNull { JWT_ISSUER = it }
    config.jwtSecret.ifNotNull { JWT_SECRET = it }
    config.jwtRealm.ifNotNull { JWT_REALM = it }

    embeddedServer(Netty, host = HOST, port = PORT) {
        configureDatabase()
        configureSecurity()
        configureContent()
        configureRouting()
    }.start(wait = true)
}

private fun <T : Any> T?.ifNotNull(block: (T) -> Unit) {
    if (this != null) {
        block(this)
    }
}