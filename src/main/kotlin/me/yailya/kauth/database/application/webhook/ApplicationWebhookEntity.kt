/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.webhook

import me.yailya.kauth.database.abstraction.IdEntityClass
import me.yailya.kauth.database.abstraction.ModeledIdEntity
import me.yailya.kauth.database.application.ApplicationEntity
import me.yailya.kauth.database.databaseQuery
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ApplicationWebhookEntity(id: EntityID<UUID>) : ModeledIdEntity<UUID, ApplicationWebhookModel>(id) {
    companion object : IdEntityClass<ApplicationWebhookEntity, UUID>(ApplicationWebhooks) {
        suspend fun create(application: ApplicationEntity, trigger: ApplicationWebhookTrigger, url: String) =
            databaseQuery {
                ApplicationWebhookEntity.new {
                    this.application = application
                    this.trigger = trigger.toString()
                    this.url = url
                }
            }
    }

    var application by ApplicationEntity referencedOn ApplicationWebhooks.application
    var trigger by ApplicationWebhooks.trigger
    var url by ApplicationWebhooks.url

    suspend fun updateEntity(block: suspend ApplicationWebhookEntity.() -> Unit) = databaseQuery {
        block(this)
        this
    }

    suspend fun deleteEntity() = databaseQuery { delete() }

    override suspend fun toModel() = databaseQuery {
        ApplicationWebhookModel(
            id.value,
            application.id.value,
            ApplicationWebhookTrigger.valueOf(trigger),
            url
        )
    }
}