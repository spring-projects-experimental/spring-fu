package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlDatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlR2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for R2DBC Postgresql configuration.
 *
 * Enable and [configure][PostgresqlR2dbcDsl] R2DBC support by registering a [org.springframework.data.r2dbc.function.DatabaseClient]
 * (or a [org.springframework.data.r2dbc.function.CoDatabaseClient] for Coroutines) bean.
 *
 * Required dependencies are `io.r2dbc:r2dbc-postgresql` and `org.springframework.data:spring-data-r2dbc`.
 *
 * @sample org.springframework.fu.kofu.samples.r2dbcPostgresql
 * @sample org.springframework.fu.kofu.samples.r2dbcPostgresqlCoroutines
 * @author Sebastien Deleuze
 */
class PostgresqlR2dbcDsl(private val init: PostgresqlR2dbcDsl.() -> Unit) : AbstractDsl() {

    /**
     * Enable coroutines support when set to `true` (register a [org.springframework.data.r2dbc.function.CoDatabaseClient] bean
     * instead of a [org.springframework.data.r2dbc.function.DatabaseClient] one). By default, set to `false`.
     */
    var coroutines: Boolean = false

    /**
     * Configure the host, by default set to `localhost`.
     */
    var host: String = "localhost"

    /**
     * Configure the port, by default set to `5432`.
     */
    var port: Int = 5432

    /**
     * Configure the database name, by default set to `postgres`.
     */
    var database: String = "postgres"

    /**
     * Configure the username, by default set to `postgres`.
     */
    var username: String = "postgres"

    /**
     * Configure the password, empty by default.
     */
    var password: String = ""

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()

        val properties = PostgresqlR2dbcProperties()
        properties.host = host
        properties.port = port
        properties.database = database
        properties.username = username
        properties.password = password
        properties.coroutines = coroutines

        PostgresqlDatabaseClientInitializer(properties).initialize(context)
    }
}

/**
 * Configure R2DBC Postgresql support.
 * @see PostgresqlR2dbcDsl
 */
fun ConfigurationDsl.r2dbcPostgresql(dsl: PostgresqlR2dbcDsl.() -> Unit = {}) {
    PostgresqlR2dbcDsl(dsl).initialize(context)
}