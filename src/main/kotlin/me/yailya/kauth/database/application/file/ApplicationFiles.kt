/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.file

import me.yailya.kauth.database.application.Applications
import org.jetbrains.exposed.dao.id.UUIDTable

object ApplicationFiles : UUIDTable() {
    val application = reference("application", Applications)
    val fileName = text("file_name")
}