/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.abstraction

import me.yailya.kauth.database.application.user.ApplicationUserEntity
import me.yailya.kauth.database.databaseQuery
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.*

abstract class IdEntityClass<E : Entity<ID>, ID : Comparable<ID>>(table: IdTable<ID>) : EntityClass<ID, E>(table) {
    suspend fun isEmpty() = databaseQuery {
        all().empty()
    }

    suspend fun getAll() = databaseQuery {
        all()
    }

    suspend fun getById(id: ID) = databaseQuery {
        find { table.id eq id }.single()
    }

    suspend fun getByOp(op: SqlExpressionBuilder.() -> Op<Boolean>) = databaseQuery {
        find(op).singleOrNull()
    }

    suspend fun deleteById(id: UUID) = databaseQuery {
        ApplicationUserEntity.getById(id)
    }
}