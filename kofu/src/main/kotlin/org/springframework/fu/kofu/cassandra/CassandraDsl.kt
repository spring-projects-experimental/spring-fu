package org.springframework.fu.kofu.cassandra

import org.springframework.boot.autoconfigure.cassandra.CassandraInitializer
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.fu.kofu.webmvc.WebMvcServerDsl

/**
 * Kofu DSL for Cassandra configuration.
 *
 * Enable and configure Cassandra support by registering [org.springframework.data.cassandra.core.CassandraTemplate]
 *
 * @sample org.springframework.fu.kofu.samples.cassandra
 */
open class CassandraDsl(private val init: CassandraDsl.() -> Unit) : AbstractDsl() {
	protected val properties = CassandraProperties()

	/**
	 * Configure the local datacenter to use.
	 */
	var localDatacenter: String?
		get() = properties.localDatacenter
		set(value) {
			properties.localDatacenter = value
		}


	/**
	 * Configure the keyspace name to use.
	 */
	var keyspaceName: String?
		get() = properties.keyspaceName
		set(value) {
			properties.keyspaceName = value
		}

	/**
	 * Configure the session name.
	 */
	var sessionName: String?
		get() = properties.sessionName
		set(value) {
			properties.sessionName = value
		}

	/**
	 * Configure the login user of the server.
	 */
	var username: String?
		get() = properties.username
		set(value) {
			properties.username = value
		}

	/**
	 * Configure the login password of the server.
	 */
	var password: String?
		get() = properties.password
		set(value) {
			properties.password = value
		}

	/**
	 * Configure the cluster node addresses.
	 */
	var contactPoints: List<String>
		get() = properties.contactPoints
		set(value) {
			properties.contactPoints.clear()
			properties.contactPoints.addAll(value)
		}

	/**
	 * Enable SSL support
	 */
	var ssl: Boolean
		get() = properties.isSsl
		set(value) {
			properties.isSsl = value
		}

	/**
	 * Configure the compression supported by the Cassandra binary protocol.
	 */
	var compression: CassandraProperties.Compression
		get() = properties.compression
		set(value) {
			properties.compression = value
		}

	/**
	 * Configure the connection.
	 */
	fun connection(dsl: CassandraProperties.Connection.() -> Unit =  {}) {
		properties.connection.dsl()
	}

	/**
	 * Configure the request.
	 */
	fun request(dsl: CassandraProperties.Request.() -> Unit =  {}) {
		properties.request.dsl()
	}

	/**
	 * Configure the pool.
	 */
	fun pool(dsl: CassandraProperties.Pool.() -> Unit =  {}) {
		properties.pool.dsl()
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		CassandraInitializer(properties).initialize(context)
		CassandraDataInitializer(properties).initialize(context)
	}
}

/**
 * Configure Cassandra support.
 * @see CassandraDsl
 */
fun ConfigurationDsl.cassandra(dsl: CassandraDsl.() -> Unit = {}) {
	CassandraDsl(dsl).initialize(context)
}
