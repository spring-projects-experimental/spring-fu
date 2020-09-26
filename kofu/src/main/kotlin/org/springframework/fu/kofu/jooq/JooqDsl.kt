package org.springframework.fu.kofu.jooq

import org.jooq.SQLDialect
import org.springframework.boot.autoconfigure.jdbc.*
import org.springframework.boot.autoconfigure.jooq.JooqConfigurationInitializer
import org.springframework.boot.autoconfigure.jooq.JooqProperties
import org.springframework.boot.jdbc.DataSourceInitializationMode
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.fu.kofu.jdbc.DataSourceType

/**
 * @author Kevin Davin
 */
class JooqDsl(private val datasourceType: DataSourceType, private val init: JooqDsl.() -> Unit) : AbstractDsl() {

    var schema = listOf<String>()

    var data = listOf<String>()

    var initializationMode = DataSourceInitializationMode.EMBEDDED

    var url: String? = null

    var name: String? = null

    var username: String? = null

    var password: String? = null

    var generateUniqueName: Boolean = true

    var sqlDialect: SQLDialect? = null

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()

        val jdbcProperties = JdbcProperties()
        val dataSourceProperties = DataSourceProperties().apply {
            schema = this@JooqDsl.schema
            data = this@JooqDsl.data
            initializationMode = this@JooqDsl.initializationMode
            url = this@JooqDsl.url
            name = this@JooqDsl.name
            username = this@JooqDsl.username
            password = this@JooqDsl.password
            isGenerateUniqueName = this@JooqDsl.generateUniqueName
        }
        val jooqProperties = JooqProperties().apply {
            sqlDialect = this@JooqDsl.sqlDialect
        }

        when(datasourceType) {
            DataSourceType.Hikari -> DataSourceConfiguration_HikariInitializer(dataSourceProperties).initialize(context)
            DataSourceType.Embedded -> EmbeddedDataSourceConfigurationInitializer(dataSourceProperties).initialize(context)
            DataSourceType.Generic -> DataSourceConfiguration_GenericInitializer(dataSourceProperties).initialize(context)
        }
        JdbcTemplateConfigurationInitializer(jdbcProperties).initialize(context)
        JooqConfigurationInitializer(jooqProperties).initialize(context)
        DataSourceTransactionManagerAutoConfigurationInitializer().initialize(context)
        DataSourceInitializerInvokerInitializer(dataSourceProperties).initialize(context)
    }
}

/**
 * Configure JOOQ support.
 * @see JooqDsl
 */
fun ConfigurationDsl.jooq(datasourceType: DataSourceType, dsl: JooqDsl.() -> Unit = {}) {
    JooqDsl(datasourceType, dsl).initialize(context)
}
