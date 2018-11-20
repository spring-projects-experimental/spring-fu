package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.CoDatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.DatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for R2DBC configuration.
 * @author Sebastien Deleuze
 */
open class R2dbcDsl(private val init: R2dbcDsl.() -> Unit) : AbstractDsl() {

    var coroutines: Boolean = false

    var host: String = "localhost"

    var port: Int = 5432

    var database: String = "postgres"

    var username: String = "postgres"

    var password: String = ""

    override fun register(context: GenericApplicationContext) {
        init()

        val properties = R2dbcProperties()
        properties.host = host
        properties.port = port
        properties.database = database
        properties.username = username
        properties.password = password

        if (coroutines)
            CoDatabaseClientInitializer(properties).initialize(context)
        else
            DatabaseClientInitializer(properties).initialize(context)
    }
}

/**
 * Enable and [configure][R2dbcDsl] R2DBC support by registering a [org.springframework.data.r2dbc.function.DatabaseClient]
 * (or a [org.springframework.data.r2dbc.function.CoDatabaseClient] for Coroutines) bean.
 *
 * @sample org.springframework.fu.kofu.samples.r2dbc
 * @sample org.springframework.fu.kofu.samples.r2dbcCoroutines
 */
fun ConfigurationDsl.r2dbc(dsl: R2dbcDsl.() -> Unit = {}) {
    addInitializer(R2dbcDsl(dsl))
}