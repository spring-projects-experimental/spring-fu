package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlDatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlR2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for R2DBC Postgresql configuration.
 *
 * Enable and configure R2DBC support by registering a [org.springframework.data.r2dbc.core.DatabaseClient] bean.
 *
 * Required dependencies are `io.r2dbc:r2dbc-postgresql` and `org.springframework.data:spring-data-r2dbc`.
 *
 * @sample org.springframework.fu.kofu.samples.r2dbcPostgresql
 * @author Sebastien Deleuze
 */
class PostgresqlR2dbcDsl(private val init: PostgresqlR2dbcDsl.() -> Unit) : AbstractDsl() {

	/**
	 * Configure the host, by default set to `localhost`.
	 */
	var host: String = "localhost"

	/**
	 * Configure the port, by default set to `5432`.
	 */
	var port: Int = 5432

	/**
	 * Configure the database, by default set to `postgres`.
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