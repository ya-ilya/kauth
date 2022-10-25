/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application

import kotlinx.serialization.Serializable
import me.yailya.kauth.database.application.user.ApplicationUserModel
import me.yailya.kauth.database.application.webhook.ApplicationWebhookModel
import me.yailya.kauth.serializers.UUIDSerializer
import java.util.*

@Serializable
data class ApplicationModel(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @Serializable(UUIDSerializer::class)
    val owner: UUID,
    val name: String,
    val users: List<ApplicationUserModel>,
    val webhooks: List<ApplicationWebhookModel>
)