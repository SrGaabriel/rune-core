package com.runerealms.core.config

import kotlinx.serialization.Serializable

@Serializable
public data class CoreConfig(
    val database: DatabaseConfig
) {
    @Serializable
    public data class DatabaseConfig(
        val host: String,
        val port: Int,
        val username: String,
        val password: String,
        val database: String
    )

    public companion object {
        public val Default: CoreConfig = CoreConfig(
            database = DatabaseConfig(
                host = "localhost",
                port = 3306,
                username = "root",
                password = "password",
                database = "database"
            )
        )
    }
}