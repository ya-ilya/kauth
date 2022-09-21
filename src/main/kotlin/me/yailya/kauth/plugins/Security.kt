/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import me.yailya.kauth.JWT_AUDIENCE
import me.yailya.kauth.JWT_ISSUER
import me.yailya.kauth.JWT_REALM
import me.yailya.kauth.JWT_SECRET

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = JWT_REALM

            verifier(
                JWT
                    .require(Algorithm.HMAC256(JWT_SECRET))
                    .withAudience(JWT_AUDIENCE!!)
                    .withIssuer(JWT_ISSUER!!)
                    .build()
            )

            validate { credential ->
                if (
                    credential.payload.audience.contains(JWT_AUDIENCE!!)
                    && credential.payload.getClaim("id") != null
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    hashMapOf("error" to "InvalidToken", "message" to "Token is not valid or has expired")
                )
            }
        }
    }
}