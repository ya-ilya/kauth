/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.file

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.yailya.kauth.database.abstraction.IdEntityClass
import me.yailya.kauth.database.abstraction.ModeledIdEntity
import me.yailya.kauth.database.application.ApplicationEntity
import me.yailya.kauth.database.databaseQuery
import org.jetbrains.exposed.dao.id.EntityID
import java.io.File
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ApplicationFileEntity(id: EntityID<UUID>) : ModeledIdEntity<UUID, ApplicationFileModel>(id) {
    companion object : IdEntityClass<ApplicationFileEntity, UUID>(ApplicationFiles) {
        suspend fun create(application: ApplicationEntity, fileName: String, byteArray: ByteArray) = databaseQuery {
            val entity = ApplicationFileEntity.new {
                this.application = application
                this.fileName = fileName
            }

            withContext(Dispatchers.IO) {
                val file = entity.file

                file.createNewFile()
                file.writeBytes(byteArray)
            }

            return@databaseQuery entity
        }
    }

    var application by ApplicationEntity referencedOn ApplicationFiles.application
    var fileName by ApplicationFiles.fileName

    val file get() = File("data/files/${id}.${fileName}")

    suspend fun updateEntity(block: suspend ApplicationFileEntity.() -> Unit) = databaseQuery {
        block(this)
        this
    }

    suspend fun deleteEntity() = databaseQuery { delete() }

    override suspend fun toModel() = databaseQuery {
        ApplicationFileModel(id.value, application.id.value, fileName)
    }
}