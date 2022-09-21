/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*

fun Application.configureContent() {
    install(Resources)
    install(ContentNegotiation) {
        json()
    }
}