package com.runerealms.core.feature.database.repository

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.transactions.transaction

public abstract class SqlRepository<Key : Comparable<Key>, Value : Entity<Key>>(
    protected val entityClass: EntityClass<Key, Value>,
): Repository<Key, Value>, SpeciallyEditedRepository<Key, Value> {
    final override fun create(key: Key, value: Value) {
        error("Create operation not supported for SQL repositories")
    }

    public abstract fun insert(key: Key, scope: (Value) -> Unit): Value

    public abstract fun <T> transactionScope(block: () -> T): T
}
public abstract class DirectSqlRepository<Key : Comparable<Key>, Value : Entity<Key>>(
    entityClass: EntityClass<Key, Value>,
): SqlRepository<Key, Value>(entityClass) {
    override fun get(key: Key): Value? = transactionScope {
        entityClass.findById(key)
    }

    override fun insert(key: Key, scope: (Value) -> Unit): Value {
        return transactionScope {
            entityClass.new(key) {
                scope(this)
            }
        }
    }

    override fun edit(value: Value, edit: (Value) -> Unit): Unit = transactionScope {
        edit(value)
    }

    override fun remove(key: Key): Unit = transactionScope {
        entityClass.findById(key)?.delete()
    }
}

public abstract class BlockingSqlRepository<Key : Comparable<Key>, Value : Entity<Key>>(
    entityClass: EntityClass<Key, Value>,
): SqlRepository<Key, Value>(entityClass) {
    override fun <T> transactionScope(block: () -> T): T =
        transaction {
            block()
        }
}