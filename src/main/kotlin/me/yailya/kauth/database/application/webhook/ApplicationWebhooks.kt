/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.webhook

import me.yailya.kauth.database.application.Applications
import org.jetbrains.exposed.dao.id.UUIDTable

object ApplicationWebhooks : UUIDTable() {
    val application = reference("application", Applications)
    val trigger = text("trigger")
    val url = text("url")
}