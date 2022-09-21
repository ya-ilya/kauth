/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application

import me.yailya.kauth.database.user.Accounts
import org.jetbrains.exposed.dao.id.UUIDTable

object Applications : UUIDTable() {
    val owner = reference("owner", Accounts)
    val name = varchar("name", 16)
}