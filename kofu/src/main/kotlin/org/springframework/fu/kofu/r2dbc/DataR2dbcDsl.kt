package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

class DataR2dbcDsl(private val init: DataR2dbcDsl.() -> Unit) : AbstractDsl() {

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()
        R2dbcDataInitializer().initialize(context)
    }
}

/**
 * Configure R2DBC support.
 * @see R2dbcDsl
 */
fun ConfigurationDsl.dataR2dbc(r2dbcDsl: DataR2dbcDsl.() -> Unit = {}) {
    DataR2dbcDsl(r2dbcDsl).initialize(context)
}