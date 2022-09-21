/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.user

import org.jetbrains.exposed.dao.id.UUIDTable

object Accounts : UUIDTable() {
    val username = varchar("username", 16)
    val email = varchar("email", 32)
    val password = varchar("password", 16)
    val token = text("token")
}