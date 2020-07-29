package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.MssqlDatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.MssqlR2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import java.time.Duration

/**
 * Kofu DSL for R2DBC MSSQL configuration.
 *
 * Enable and configure R2DBC support by registering a [org.springframework.data.r2dbc.core.DatabaseClient] bean.
 *
 * Required dependencies are `io.r2dbc:r2dbc-mssql` and `org.springframework.data:spring-data-r2dbc`.
 *
 * @sample org.springframework.fu.kofu.samples.r2dbcMssql
 * @author Jonas Bark
 */
class MssqlR2dbcDsl(private val init: MssqlR2dbcDsl.() -> Unit) : AbstractDsl() {

	/**
	 * Configure the host, by default set to `localhost`.
	 */
	var host: String = "localhost"

	/**
	 * Configure the port, by default set to `1433`.
	 */
	var port: Int = 1433

	/**
	 * Configure the database, by default set to `master`.
	 */
	var database: String = "master"

	/**
	 * Configure the username, by default set to `SA`.
	 */
	var username: String = "SA"

	/**
	 * Configure the password, empty by default.
	 */
	var password: String = ""

	/**
	 * Configure whether to prefer cursored execution.
	 */
	var preferCursoredExecution: Boolean = false

	/**
	 * Configure the connect timeout. Defaults to 30 seconds.
	 */
	var connectTimeout: Duration = Duration.ofSeconds(30)

	/**
	 * Enable or disable SSL usage. This flag is also known as Use Encryption in other drivers.
	 */
	var ssl: Boolean = false

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()

		val properties = MssqlR2dbcProperties()
		properties.host = host
		properties.port = port
		properties.database = database
		properties.username = username
		properties.password = password
		properties.isPreferCursoredExecution = preferCursoredExecution
		properties.connectTimeout = connectTimeout
		properties.isSsl = ssl

		MssqlDatabaseClientInitializer(properties).initialize(context)
	}
}

/**
 * Configure R2DBC MSSQL support.
 * @see MssqlR2dbcDsl
 */
fun ConfigurationDsl.r2dbcMssql(dsl: MssqlR2dbcDsl.() -> Unit = {}) {
	MssqlR2dbcDsl(dsl).initialize(context)
}
