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

    private fun createConnectionFromConfig(config: CoreConfig): DatabaseConnection {
        when (config.database.type.lowercase()) {
            "postgresql" -> {
                val postgre = config.database.database ?: error("Database configuration not found")
                return PostgreDatabaseConnection(
                    host = postgre.host,
                    port = postgre.port.toString(),
                    database = postgre.database,
                    username = postgre.username,
                    password = postgre.password
                )
            }
            "mysql" -> {
                val mysql = config.database.database ?: error("Database configuration not found")
                return MysqlDatabaseConnection(
                    host = mysql.host,
                    port = mysql.port.toString(),
                    database = mysql.database,
                    username = mysql.username,
                    password = mysql.password
                )
            }
            "sqlite" -> {
                val sqlitePath = config.database.sqlite ?: error("Sqlite path not found")
                return SqliteDatabaseConnection(
                    path = CorePlugin.instance.dataFolder.absolutePath + "/" + sqlitePath
                )
            }
            else -> error("Database type not supported")
        }
    }

    public companion object: RuneFeature<Databases>() {
        override fun create(plugin: RunePlugin): Databases =
            Databases(plugin)
    }
}