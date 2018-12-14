package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlDatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlR2dbcProperties
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for R2DBC configuration.
 * @author Sebastien Deleuze
 */
open class PostgresqlR2dbcDsl(private val init: PostgresqlR2dbcDsl.() -> Unit) : AbstractDsl() {

    var coroutines: Boolean = false

    var host: String = "localhost"

    var port: Int = 5432

    var database: String = "postgres"

    var username: String = "postgres"

    var password: String = ""

    override fun register() {
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
 * Enable and [configure][PostgresqlR2dbcDsl] R2DBC support by registering a [org.springframework.data.r2dbc.function.DatabaseClient]
 * (or a [org.springframework.data.r2dbc.function.CoDatabaseClient] for Coroutines) bean.
 *
 * @sample org.springframework.fu.kofu.samples.r2dbcPostgresql
 * @sample org.springframework.fu.kofu.samples.r2dbcPostgresqlCoroutines
 */
fun ConfigurationDsl.r2dbcPostgresql(dsl: PostgresqlR2dbcDsl.() -> Unit = {}) {
    PostgresqlR2dbcDsl(dsl).initialize(context)
}