package org.springframework.fu.kofu.jdbc

import org.springframework.boot.autoconfigure.jdbc.*
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

class JdbcDsl(private val init: JdbcDsl.() -> Unit) : AbstractDsl() {

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()

        val jdbcProperties = JdbcProperties()
        val dataSourceProperties = DataSourceProperties()

        EmbeddedDataSourceConfigurationInitializer(dataSourceProperties).initialize(context)
        JdbcTemplateConfigurationInitializer(jdbcProperties).initialize(context)
        DataSourceTransactionManagerAutoConfigurationInitializer().initialize(context)
        DataSourceInitializerInvokerInitializer(dataSourceProperties).initialize(context)
    }

}

/**
 * Configure JDBC support.
 * @see JdbcDsl
 */
fun ConfigurationDsl.jdbc(dsl: JdbcDsl.() -> Unit = {}) {
    JdbcDsl(dsl).initialize(context)
}
