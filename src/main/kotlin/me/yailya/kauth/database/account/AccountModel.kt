/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.account

import kotlinx.serialization.Serializable
import me.yailya.kauth.serializers.UUIDSerializer
import java.util.*

@Serializable
data class AccountModel(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val username: String
)