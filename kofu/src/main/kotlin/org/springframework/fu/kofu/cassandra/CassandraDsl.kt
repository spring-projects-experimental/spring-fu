package org.springframework.fu.kofu.cassandra

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.ProtocolOptions
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
 * @sample org.springframework.fu.kofu.samples.cassandra
 */
open class CassandraDsl(private val init: CassandraDsl.() -> Unit) : AbstractDsl() {
	protected val properties = CassandraProperties()

	/**
	 * Configure the keyspace name to use.
	 */
	var keyspaceName: String?
		get() = properties.keyspaceName
		set(value) {
			properties.keyspaceName = value
		}

	/**
	 * Configure the name of the Cassandra cluster.
	 */
	var clusterName: String?
		get() = properties.clusterName
		set(value) {
			properties.clusterName = value
		}

	/**
	 * Configure the port of the Cassandra server.
	 */
	var port: Int
		get() = properties.port
		set(value) {
			properties.port = value
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
	 * Configure the cluster node addresses. The list can not be reassigned.
	 */
	val contactPoints: MutableList<String>
		get() = properties.contactPoints

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
	var compression: ProtocolOptions.Compression?
		get() = properties.compression
		set(value) {
			properties.compression = value
		}

	/**
	 * Configure the queries consistency level.
	 */
	var consistencyLevel: ConsistencyLevel?
		get() = properties.consistencyLevel
		set(value) {
			properties.consistencyLevel = value
		}

	/**
	 * Configure the queries serial consistency level.
	 */
	var serialConsistencyLevel: ConsistencyLevel?
		get() = properties.serialConsistencyLevel
		set(value) {
			properties.serialConsistencyLevel = value
		}

	/**
	 * Configure the queries default fetch size.
	 */
	var fetchSize: Int
		get() = properties.fetchSize
		set(value) {
			properties.fetchSize = value
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
	 * Configure whether to enable JMX reporting. Default to false as Cassandra JMX reporting is not
	 * compatible with Dropwizard Metrics.
	 */
	var jmxEnabled: Boolean
		get() = properties.isJmxEnabled
		set(value) {
			properties.isJmxEnabled = value
		}

	/**
	 * Retrieve the pool configuration.
	 */
	val pool: CassandraProperties.Pool?
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
