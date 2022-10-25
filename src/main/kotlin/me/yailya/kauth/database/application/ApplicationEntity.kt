/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application

import me.yailya.kauth.database.abstraction.IdEntityClass
import me.yailya.kauth.database.abstraction.ModeledIdEntity
import me.yailya.kauth.database.account.AccountEntity
import me.yailya.kauth.database.application.user.ApplicationUserEntity
import me.yailya.kauth.database.application.user.ApplicationUsers
import me.yailya.kauth.database.application.webhook.ApplicationWebhookEntity
import me.yailya.kauth.database.application.webhook.ApplicationWebhookTrigger
import me.yailya.kauth.database.application.webhook.ApplicationWebhooks
import me.yailya.kauth.database.databaseQuery
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
    val webhooks by ApplicationWebhookEntity referrersOn ApplicationWebhooks.application
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

    suspend fun createApplicationWebhook(trigger: String, url: String) =
        ApplicationWebhookEntity.create(this, ApplicationWebhookTrigger.valueOf(trigger), url)

    suspend fun getApplicationWebhook(id: UUID) = databaseQuery {
        webhooks.find { it.id.value == id }!!
    }

    suspend fun getApplicationWebhookModels() = databaseQuery {
        webhooks.map { it.toModel() }
    }

    suspend fun getApplicationWebhooks() = databaseQuery { webhooks }

    suspend fun deleteApplicationWebhook(id: UUID) = databaseQuery {
        getApplicationWebhook(id).delete()
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
            users.map { it.toModel() },
            webhooks.map { it.toModel() }
        )
    }
}