package com.runerealms.core.config

import kotlinx.serialization.Serializable

@Serializable
public data class CoreConfig(
    val database: DatabaseConfig
) {
    @Serializable
    public data class DatabaseConfig(
        val type: String,
        val sqlite: String? = null,
        val database: DatabaseStructure? = null,
    )

    @Serializable
    public data class DatabaseStructure(
        val host: String,
        val port: Int,
        val username: String,
        val password: String,
        val database: String
    )

    public companion object {
        public val Default: CoreConfig = CoreConfig(
            database = DatabaseConfig(
                type = "sqlite",
                sqlite = "database.db"
            )
        )
    }
}