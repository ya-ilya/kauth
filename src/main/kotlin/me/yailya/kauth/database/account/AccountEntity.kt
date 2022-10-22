/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.account

import me.yailya.kauth.database.abstraction.IdEntityClass
import me.yailya.kauth.database.abstraction.ModeledIdEntity
import me.yailya.kauth.database.application.ApplicationEntity
import me.yailya.kauth.database.application.Applications
import me.yailya.kauth.database.databaseQuery
import me.yailya.kauth.exceptions.PrintableException
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
class AccountEntity(id: EntityID<UUID>) : ModeledIdEntity<UUID, AccountModel>(id) {
    companion object : IdEntityClass<AccountEntity, UUID>(Accounts) {
        suspend fun create(username: String, email: String, password: String) = databaseQuery {
            if (!AccountEntity.find { Accounts.email eq email }.empty()) {
                throw PrintableException("AccountAlreadyExist", "User with same email already exist")
            }

            AccountEntity.new {
                this.username = username
                this.email = email
                this.password = password
                this.token = ""
            }
        }
    }

    var username by Accounts.username
    var email by Accounts.email
    var password by Accounts.password
    var token by Accounts.token
    val applications by ApplicationEntity referrersOn Applications.owner

    suspend fun getApplication(id: UUID) = databaseQuery {
        applications.find { it.id.value == id }!!
    }

    suspend fun getApplications() = databaseQuery { applications }

    suspend fun getApplicationModels() = databaseQuery {
        applications.map { it.toModel() }
    }

    suspend fun createApplication(name: String) = ApplicationEntity.create(this, name)

    suspend fun deleteApplication(id: UUID) {
        getApplication(id).delete()
    }

    suspend fun updateEntity(block: suspend AccountEntity.() -> Unit) = databaseQuery {
        block(this)
        this
    }

    suspend fun deleteEntity() = databaseQuery { delete() }

    override suspend fun toModel() = databaseQuery {
        AccountModel(id.value, username)
    }
}