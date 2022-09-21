/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.user

import kotlinx.serialization.Serializable
import me.yailya.kauth.serializers.UUIDSerializer
import java.util.*

@Serializable
@Suppress("SpellCheckingInspection")
data class ApplicationUserModel(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @Serializable(UUIDSerializer::class)
    val applicationId: UUID,
    val key: String,
    val hwid: String
)