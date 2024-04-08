package com.runerealms.core.feature.database.repository

import io.github.reactivecircus.cache4k.Cache
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.transactions.transaction

public class CachedSqlRepository<Key : Comparable<Key>, Value : Entity<Key>>(
    entityClass: EntityClass<Key, Value>,
    cacheBuilder: Cache.Builder<Key, Value>,
): SqlRepository<Key, Value>(entityClass) {
    public val cache: Cache<Key, Value> = cacheBuilder.build()

    override fun <T> transactionScope(block: () -> T): T = transaction {
        block()
    }

    override fun insert(key: Key, scope: (Value) -> Unit): Value = transactionScope {
        entityClass.new(key) {
            scope(this)
        }
    }

    override fun edit(value: Value, edit: (Value) -> Unit) {
        transactionScope {
            edit(value)
        }
        cache.put(value.id.value, value)
    }

    override fun remove(key: Key) {
        transactionScope {
            (cache.get(key) ?: entityClass.findById(key))?.delete()
        }
        cache.invalidate(key)
    }

    override fun get(key: Key): Value? =
        cache.get(key) ?: transactionScope {
            entityClass.findById(key)
        }
}