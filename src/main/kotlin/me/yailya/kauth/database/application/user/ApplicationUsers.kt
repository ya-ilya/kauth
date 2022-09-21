/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.user

import me.yailya.kauth.database.application.Applications
import org.jetbrains.exposed.dao.id.UUIDTable

@Suppress("SpellCheckingInspection")
object ApplicationUsers : UUIDTable() {
    val application = reference("application", Applications)
    val key = text("key")
    val hwid = text("hwid")
}