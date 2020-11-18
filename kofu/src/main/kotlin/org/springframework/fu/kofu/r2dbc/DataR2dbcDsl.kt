package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

class DataDsl(private val init: DataDsl.() -> Unit) : AbstractDsl() {

    private var r2dbc: R2dbcDsl? = null

    /**
     * Configure R2DBC support for
     * @see R2dbcDsl
     */
    fun r2dbc(dsl: R2dbcDsl.() -> Unit = {}) {
        r2dbc = R2dbcDsl(dsl)
    }

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()

        r2dbc?.let {
            it.initialize(context)
            R2dbcDataInitializer().initialize(context)
        }
    }
}

/**
 * Configure R2DBC support.
 * @see R2dbcDsl
 */
fun ConfigurationDsl.dataR2dbc(dsl: DataDsl.() -> Unit = {}) {
    DataDsl(dsl).initialize(context)
}