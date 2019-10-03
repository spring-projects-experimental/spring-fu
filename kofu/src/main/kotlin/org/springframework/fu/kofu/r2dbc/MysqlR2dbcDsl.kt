package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.MysqlDatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.MysqlR2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for R2DBC MySQL configuration.
 *
 * Enable and configure R2DBC support by registering a [org.springframework.data.r2dbc.core.DatabaseClient] bean.
 *
 * Required dependencies are `com.github.jasync-sql:jasync-r2dbc-mysql` and `org.springframework.data:spring-data-r2dbc`.
 *
 * @sample org.springframework.fu.kofu.samples.r2dbcMysql
 * @author Jonas Bark
 */
class MysqlR2dbcDsl(
	/**
	 * Configure the connection URL
	 */
	private val url: String,
	private val init: MysqlR2dbcDsl.() -> Unit
) : AbstractDsl() {

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()

		val properties = MysqlR2dbcProperties()
		properties.url = url

		MysqlDatabaseClientInitializer(properties).initialize(context)
	}
}

/**
 * Configure R2DBC MSSQL support.
 * @see MssqlR2dbcDsl
 */
fun ConfigurationDsl.r2dbcMysql(url: String, dsl: MysqlR2dbcDsl.() -> Unit = {}) {
	MysqlR2dbcDsl(url, dsl).initialize(context)
}
