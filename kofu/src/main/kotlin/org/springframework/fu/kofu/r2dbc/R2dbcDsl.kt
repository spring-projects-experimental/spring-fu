package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer
import org.springframework.boot.autoconfigure.r2dbc.R2dbcInitializer
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import java.util.*

class R2dbcDsl(private val init: R2dbcDsl.() -> Unit) : AbstractDsl() {

    var name: String? = null

    var generateUniqueName: Boolean = false

    var url: String? = null

    var username: String? = null

    var password: String? = null

    var properties: Map<String, String> = LinkedHashMap()

    var optionsCustomizers: List<ConnectionFactoryOptionsBuilderCustomizer> = emptyList()

    var transactional: Boolean = false

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()

        val properties = r2dbcProperties()

        R2dbcInitializer(properties, optionsCustomizers, transactional).initialize(context)
    }

    fun r2dbcProperties() : R2dbcProperties =
        let  { self -> R2dbcProperties().apply {
                name = self.name
                generateUniqueName = self.generateUniqueName
                url = self.url
                username = self.username
                password = self.password
                properties.putAll(self.properties)
            }
        }

    fun customize(optionCustomizer: ConnectionFactoryOptionsBuilderCustomizer){
        optionsCustomizers += optionCustomizer
    }
}

/**
 * Configure R2DBC support.
 * @see R2dbcDsl
 */
fun ConfigurationDsl.r2dbc(dsl: R2dbcDsl.() -> Unit = {}) {
    R2dbcDsl(dsl).initialize(context)
}

fun DataR2dbcDsl.r2dbc(dsl: R2dbcDsl.() -> Unit = {}) {
    R2dbcDsl(dsl).initialize(context)
}
