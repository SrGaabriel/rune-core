package com.runerealms.core.feature.database.repository

public interface Repository<Key : Any, Value : Any> {
    public fun create(key: Key, value: Value)
    public operator fun get(key: Key): Value?
    public fun update(key: Key, value: Value)
    public fun remove(key: Key)
}

public interface SpeciallyEditedRepository<Key:  Any, Value : Any>: Repository<Key, Value> {
    public fun edit(value: Value, edit: (Value) -> Unit)

    override fun update(key: Key, value: Value) {
        error("Update operation not supported for Specially Edited Repositories")
    }
}