/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.abstraction

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID

abstract class ModeledIdEntity<ID : Comparable<ID>, M : Any>(id: EntityID<ID>) : Entity<ID>(id) {
    abstract suspend fun toModel(): M
}