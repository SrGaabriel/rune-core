package com.runerealms.core.feature.database

import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager


public interface DatabaseConnection {
    public fun connect()
}

public class PostgreDatabaseConnection(
    private val host: String,
    private val port: String,
    private val database: String,
    private val username: String,
    private val password: String
) : DatabaseConnection {
    override fun connect() {
        val hikariDataSource = HikariDataSource().also { datasource ->
            datasource.jdbcUrl = "jdbc:postgresql://${host}:${port}/${database}?useTimezone=true&serverTimezone=UTC"
            datasource.username = username
            datasource.password = password
            datasource.driverClassName = "org.postgresql.Driver"
        }
        Database.connect(hikariDataSource)
    }
}

public class MysqlDatabaseConnection(
    private val host: String,
    private val port: String,
    private val database: String,
    private val username: String,
    private val password: String
) : DatabaseConnection {
    override fun connect() {
        val hikariDataSource = HikariDataSource().also { datasource ->
            datasource.jdbcUrl = "jdbc:mysql://${host}:${port}/${database}?useTimezone=true&serverTimezone=UTC"
            datasource.username = username
            datasource.password = password
            datasource.driverClassName = "com.mysql.cj.jdbc.Driver"
        }
        Database.connect(hikariDataSource)
    }
}

public class SqliteDatabaseConnection(
    private val path: String
) : DatabaseConnection {
    override fun connect() {
        val hikariDataSource = HikariDataSource().also { datasource ->
            datasource.jdbcUrl = "jdbc:sqlite:${path}"
            datasource.driverClassName = "org.sqlite.JDBC"
        }
        TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
        Database.connect(hikariDataSource)
    }
}