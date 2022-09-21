/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.user

import me.yailya.kauth.database.abstraction.IdEntityClass
import me.yailya.kauth.database.abstraction.ModeledIdEntity
import me.yailya.kauth.database.application.ApplicationEntity
import me.yailya.kauth.database.databaseQuery
import me.yailya.kauth.exceptions.PrintableException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused", "SpellCheckingInspection")
class ApplicationUserEntity(id: EntityID<UUID>) : ModeledIdEntity<UUID, ApplicationUserModel>(id) {
    companion object : IdEntityClass<ApplicationUserEntity, UUID>(ApplicationUsers) {
        suspend fun create(application: ApplicationEntity, key: String, hwid: String) = databaseQuery {
            if (getByOp { (ApplicationUsers.application eq application.id) and (ApplicationUsers.key eq key) } != null) {
                throw PrintableException(
                    "ApplicationUserAlreadyExist", "User with same key already exist in this application"
                )
            }

            ApplicationUserEntity.new {
                this.application = application
                this.key = key
                this.hwid = hwid
            }
        }
    }

    var application by ApplicationEntity referencedOn ApplicationUsers.application
    var key by ApplicationUsers.key
    var hwid by ApplicationUsers.hwid

    suspend fun updateEntity(block: suspend ApplicationUserEntity.() -> Unit) = databaseQuery {
        block(this)
        this
    }

    suspend fun deleteEntity() = databaseQuery { delete() }

    override suspend fun toModel() = databaseQuery {
        ApplicationUserModel(
            id.value,
            application.id.value,
            key, hwid
        )
    }
}