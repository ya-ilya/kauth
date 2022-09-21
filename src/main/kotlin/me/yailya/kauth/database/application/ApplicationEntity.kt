/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application

import me.yailya.kauth.database.abstraction.IdEntityClass
import me.yailya.kauth.database.abstraction.ModeledIdEntity
import me.yailya.kauth.database.application.user.ApplicationUserEntity
import me.yailya.kauth.database.application.user.ApplicationUsers
import me.yailya.kauth.database.databaseQuery
import me.yailya.kauth.database.user.AccountEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused", "SpellCheckingInspection")
class ApplicationEntity(id: EntityID<UUID>) : ModeledIdEntity<UUID, ApplicationModel>(id) {
    companion object : IdEntityClass<ApplicationEntity, UUID>(Applications) {
        suspend fun create(owner: AccountEntity, name: String) = databaseQuery {
            ApplicationEntity.new {
                this.owner = owner
                this.name = name
            }
        }
    }

    var owner by AccountEntity referencedOn Applications.owner
    val users by ApplicationUserEntity referrersOn ApplicationUsers.application
    var name by Applications.name

    suspend fun createApplicationUser(key: String, hwid: String) =
        ApplicationUserEntity.create(this, key, hwid)

    suspend fun getApplicationUser(id: UUID) = databaseQuery {
        users.find { it.id.value == id }!!
    }

    suspend fun getApplicationUserModels() = databaseQuery {
        users.map { it.toModel() }
    }

    suspend fun getApplicationUsers() = databaseQuery { users }

    suspend fun deleteApplicationUser(id: UUID) = databaseQuery {
        getApplicationUser(id).delete()
    }

    suspend fun updateEntity(block: suspend ApplicationEntity.() -> Unit) = databaseQuery {
        block(this)
        this
    }

    suspend fun deleteEntity() = databaseQuery { delete() }

    override suspend fun toModel() = databaseQuery {
        ApplicationModel(
            id.value,
            owner.id.value,
            name,
            users.map { it.toModel() }
        )
    }
}