package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.H2DatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.H2R2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for R2DBC configuration.
 * @author Sebastien Deleuze
 */
open class H2R2dbcDsl(private val init: H2R2dbcDsl.() -> Unit) : AbstractDsl() {

    var coroutines: Boolean = false

    var url: String = "mem:test;DB_CLOSE_DELAY=-1"

    var username: String? = null

    var password: String? = null

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()

        val properties = H2R2dbcProperties()
        properties.url = url
        properties.username = username
        properties.password = password
        properties.coroutines = coroutines

        H2DatabaseClientInitializer(properties).initialize(context)
    }
}

/**
 * Enable and [configure][H2R2dbcDsl] R2DBC support by registering a [org.springframework.data.r2dbc.function.DatabaseClient]
 * (or a [org.springframework.data.r2dbc.function.CoDatabaseClient] for Coroutines) bean.
 *
 * @sample org.springframework.fu.kofu.samples.r2dbcH2
 * @sample org.springframework.fu.kofu.samples.r2dbcH2Coroutines
 */
fun ConfigurationDsl.r2dbcH2(dsl: H2R2dbcDsl.() -> Unit = {}) {
    H2R2dbcDsl(dsl).initialize(context)
}