package org.springframework.fu.kofu.cassandra

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel
import org.springframework.boot.autoconfigure.cassandra.CassandraInitializer
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import java.time.Duration

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
	 * Configure the queries consistency level.
	 */
	var consistencyLevel: DefaultConsistencyLevel?
		get() = properties.consistencyLevel
		set(value) {
			properties.consistencyLevel = value
		}

	/**
	 * Configure the queries serial consistency level.
	 */
	var serialConsistencyLevel: DefaultConsistencyLevel?
		get() = properties.serialConsistencyLevel
		set(value) {
			properties.serialConsistencyLevel = value
		}

	/**
	 * Configure the queries default page size.
	 */
	var pageSize: Int
		get() = properties.pageSize
		set(value) {
			properties.pageSize = value
		}

	/**
	 * Configure the socket option: connection time out.
	 */
	var connectTimeout: Duration?
		get() = properties.connectTimeout
		set(value) {
			properties.connectTimeout = value
		}

	/**
	 * Configure the socket option: read time out.
	 */
	var readTimeout: Duration?
		get() = properties.readTimeout
		set(value) {
			properties.readTimeout = value
		}

	/**
	 * Retrieve the pool configuration.
	 */
	val pool: CassandraProperties.Pool
		get() = properties.pool

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
