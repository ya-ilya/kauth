/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import me.yailya.kauth.database.account.Accounts
import me.yailya.kauth.database.application.Applications
import me.yailya.kauth.database.application.file.ApplicationFiles
import me.yailya.kauth.database.application.user.ApplicationUsers
import me.yailya.kauth.database.application.webhook.ApplicationWebhooks
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

val folders = arrayOf("data", "data/files")

@Suppress("UnusedReceiverParameter")
fun Application.configureDatabase() {
    folders.map { File(it) }.forEach { if (!it.exists()) it.mkdirs() }

    val driverClassName = "org.h2.Driver"
    val jdbcURL = "jdbc:h2:file:./data/base"
    val database = org.jetbrains.exposed.sql.Database.connect(jdbcURL, driverClassName)

    transaction(database) {
        SchemaUtils.create(Accounts, Applications, ApplicationUsers, ApplicationWebhooks, ApplicationFiles)

        addLogger(StdOutSqlLogger)
    }
}

suspend fun <T> databaseQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }