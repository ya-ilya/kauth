/*
 * Copyright (c) 2022. ya-ilya
 */

@file:Suppress("HttpUrlsUsage")

package me.yailya.kauth

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.yailya.kauth.database.configureDatabase
import me.yailya.kauth.plugins.configureContent
import me.yailya.kauth.plugins.configureRouting
import me.yailya.kauth.plugins.configureSecurity

var HOST = "127.0.0.1"
var PORT = 8080
var JWT_SECRET = "secret"
var JWT_REALM = "Access to 'kauth'"

var JWT_AUDIENCE: String? = null
    get() = field ?: "http://$HOST:$PORT/api"
var JWT_ISSUER: String? = null
    get() = field ?: "http://$HOST:$PORT/api"

fun main(args: Array<String>) {
    arguments(args, mapOf(
        "host" to { HOST = it },
        "port" to { PORT = it.toInt() },
        "jwt-audience" to { JWT_AUDIENCE = it },
        "jwt-issuer" to { JWT_ISSUER = it },
        "jwt-secret" to { JWT_SECRET = it },
        "jwt-realm" to { JWT_REALM = it }
    ))

    embeddedServer(Netty, host = HOST, port = PORT) {
        configureDatabase()
        configureSecurity()
        configureContent()
        configureRouting()
    }.start(wait = true)
}

fun arguments(args: Array<String>, map: Map<String, (String) -> Unit>) {
    for ((key, value) in map) {
        val index = args.indexOf("--$key")

        if (index != -1) {
            if (args[index + 1].startsWith("\"")) {
                val sList = mutableListOf(args[index + 1].removePrefix("\""))

                for (sIndex in (index + 1)..args.lastIndex) {
                    val sPart = args[sIndex]

                    if (sPart.endsWith("\"")) {
                        sList.add(sPart.removeSuffix("\""))
                        break
                    }

                    sList.add(sPart)
                }

                value(sList.joinToString(" "))
                return
            }

            value(args[index + 1])
        }
    }
}