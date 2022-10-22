/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val host: String? = null,
    val port: Int? = null,
    val jwtAudience: String? = null,
    val jwtIssuer: String? = null,
    val jwtSecret: String? = null,
    val jwtRealm: String? = null
)