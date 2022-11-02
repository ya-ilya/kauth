/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.file

import kotlinx.serialization.Serializable
import me.yailya.kauth.serializers.UUIDSerializer
import java.util.*

@Serializable
data class ApplicationFileModel(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @Serializable(UUIDSerializer::class)
    val applicationId: UUID,
    val fileName: String
)