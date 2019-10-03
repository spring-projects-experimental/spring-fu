package org.springframework.fu.kofu.r2dbc

import org.springframework.boot.autoconfigure.data.r2dbc.H2DatabaseClientInitializer
import org.springframework.boot.autoconfigure.data.r2dbc.H2R2dbcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for R2DBC H2 configuration.
 *
 * Enable and [configure][H2R2dbcDsl] R2DBC support by registering a [org.springframework.data.r2dbc.core.DatabaseClient]
 * bean.
 *
 * Required dependencies are `io.r2dbc:r2dbc-h2` and `org.springframework.data:spring-data-r2dbc`.

 * @sample org.springframework.fu.kofu.samples.r2dbcH2
 * @author Sebastien Deleuze
 */
class H2R2dbcDsl(private val init: H2R2dbcDsl.() -> Unit) : AbstractDsl() {

	/**
	 * Configure the database url, `mem:test;DB_CLOSE_DELAY=-1` by default.
	 * Includes everything after the `jdbc:h2:` prefix.
	 * For in-memory and file-based databases, must include the proper prefix (e.g. `file:` or `mem:`).
	 *
	 * See [http://www.h2database.com/html/features.html#database_url](http://www.h2database.com/html/features.html#database_url) for more details.
	 */
	var url: String = "mem:test;DB_CLOSE_DELAY=-1"

	/**
	 * Configure the username.
	 */
	var username: String? = null

	/**
	 * Configure the password.
	 */
	var password: String? = null

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()

		val properties = H2R2dbcProperties()
		properties.url = url
		properties.username = username
		properties.password = password

		H2DatabaseClientInitializer(properties).initialize(context)
	}
}

/**
 * Configure R2DBC H2 support.
 * @see H2R2dbcDsl
 */
fun ConfigurationDsl.r2dbcH2(dsl: H2R2dbcDsl.() -> Unit = {}) {
	H2R2dbcDsl(dsl).initialize(context)
}