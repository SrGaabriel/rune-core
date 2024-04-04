package com.runerealms.core.feature.database

import com.runerealms.core.CorePlugin
import com.runerealms.core.RunePlugin
import com.runerealms.core.config.CoreConfig
import com.runerealms.core.feature.RuneFeature
import com.runerealms.core.feature.RuneFeatureInstance
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

public class Databases(plugin: RunePlugin): RuneFeatureInstance(plugin = plugin) {
    private val connections: MutableList<DatabaseConnection> = mutableListOf()
    private val afterConnectingHandlers: MutableList<(DatabaseConnection) -> Unit> = mutableListOf()

    override fun onStart() {
        connections.forEach {
            it.connect()
            afterConnectingHandlers.forEach { handler ->
                handler(it)
            }
        }
    }

    public fun connectIntoCore() {
        connections.add(createConnectionFromConfig(CorePlugin.instance.config))
    }

    public fun tables(vararg tables: Table): Unit = transaction {
        SchemaUtils.createMissingTablesAndColumns(*tables)
    }

    public fun afterConnecting(handler: (DatabaseConnection) -> Unit) {
        afterConnectingHandlers.add(handler)
    }

    private fun createConnectionFromConfig(config: CoreConfig): PostgreDatabaseConnection =
        PostgreDatabaseConnection(
            host = config.database.host,
            port = config.database.port.toString(),
            database = config.database.database,
            username = config.database.username,
            password = config.database.password
        )

    public companion object: RuneFeature<Databases>() {
        override fun create(plugin: RunePlugin): Databases =
            Databases(plugin)
    }
}