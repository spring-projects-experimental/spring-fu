package org.springframework.fu.kofu.redis

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.*
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import java.time.Duration

/**
 * Kofu DSL base Redis DSL
 *
 * @author Waldemar Panas
 */
open class AbstractRedisDsl : AbstractDsl() {
	protected val properties = RedisProperties()

	/**
	 * Configure the database index used by the connection factory.
	 */
	var database: Int
		get() = properties.database
		set(value) {
			properties.database = value
		}

	/**
	 * Configure the connection URL. Overrides host, port, and password. User is ignored.
	 * Example: redis://user:password@example.com:6379
	 */
	var url: String?
		get() = properties.url
		set(value) {
			properties.url = value
		}

	/**
	 * Configure the redis server host.
	 */
	var host: String?
		get() = properties.host
		set(value) {
			properties.host = value
		}

	/**
	 * Configure the login password of the redis server.
	 */
	var password: String?
		get() = properties.password
		set(value) {
			properties.password = value
		}

	/**
	 * Configure the redis server port.
	 */
	var port: Int
		get() = properties.port
		set(value) {
			properties.port = value
		}

	/**
	 * Configure the connection timeout.
	 */
	var timeout: Duration?
		get() = properties.timeout
		set(value) {
			properties.timeout = value
		}

	/**
	 * Configure whether to enable SSL support.
	 */
	var ssl: Boolean
		get() = properties.isSsl
		set(value) {
			properties.isSsl = value
		}

	/**
	 * Configure the redis sentinel properties via a [dedicated DSL][SentinelDsl].
	 */
	fun sentinel(dsl: SentinelDsl.() -> Unit = {}) {
		properties.sentinel = Sentinel()
		SentinelDsl(properties.sentinel, dsl).initialize(context)
	}

	/**
	 * Configure the cluster properties via a [dedicated DSL][ClusterDsl].
	 */
	fun cluster(dsl: ClusterDsl.() -> Unit = {}) {
		properties.cluster = Cluster()
		ClusterDsl(properties.cluster, dsl).initialize(context)
	}
}

/**
 * Configure the cluster properties.
 *
 * @author Waldemar Panas
 */
class ClusterDsl(private val cluster: Cluster, private val init: ClusterDsl.() -> Unit) : AbstractDsl() {

	/**
	 * Configure the cluster node.
	 */
	fun node(host: String, port: Int) {
		if (cluster.nodes == null) {
			cluster.nodes = mutableListOf()
		}
		cluster.nodes.add("$host:$port")
	}

	/**
	 * Configure the maximum number of redirects to follow when executing commands
	 * across the cluster.
	 */
	var maxRedirects: Int?
		get() = cluster.maxRedirects
		set(value) {
			cluster.maxRedirects = value
		}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
	}
}

/**
 * Configure the redis sentinel properties.
 *
 * @author Waldemar Panas
 */
class SentinelDsl(private val sentinel: Sentinel, private val init: SentinelDsl.() -> Unit) : AbstractDsl() {

	/**
	 * Configure the name of the redis server.
	 */
	var master: String?
		get() = sentinel.master
		set(value) {
			sentinel.master = value
		}

	/**
	 * Configure the sentinel node.
	 */
	fun node(host: String, port: Int) {
		if (sentinel.nodes == null) {
			sentinel.nodes = mutableListOf()
		}
		sentinel.nodes.add("$host:$port")
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
	}
}

/**
 * Configure the pool properties.
 *
 * @author Waldemar Panas
 */
open class PoolDsl(private var pool: Pool, private val init: PoolDsl.() -> Unit) : AbstractDsl() {

	/**
	 * Configure the maximum number of "idle" connections in the pool. Use a negative value to
	 * indicate an unlimited number of idle connections.
	 */
	var maxIdle: Int
		get() = pool.maxIdle
		set(value) {
			pool.maxIdle = value
		}

	/**
	 * Configure the target for the minimum number of idle connections to maintain in the pool. This
	 * setting only has an effect if both it and time between eviction runs are
	 * positive.
	 */
	var minIdle: Int
		get() = pool.minIdle
		set(value) {
			pool.minIdle = value
		}

	/**
	 * Configure the maximum number of connections that can be allocated by the pool at a given
	 * time. Use a negative value for no limit.
	 */
	var maxActive: Int
		get() = pool.maxActive
		set(value) {
			pool.maxActive = value
		}

	/**
	 * Configure the maximum amount of time a connection allocation should block before throwing an
	 * exception when the pool is exhausted. Use a negative value to block
	 * indefinitely.
	 */
	var maxWait: Duration?
		get() = pool.maxWait
		set(value) {
			pool.maxWait = value
		}

	/**
	 * Configure the time between runs of the idle object evictor thread. When positive, the idle
	 * object evictor thread starts, otherwise no idle object eviction is performed.
	 */
	var timeBetweenEvictionRuns: Duration?
		get() = pool.timeBetweenEvictionRuns
		set(value) {
			pool.timeBetweenEvictionRuns = value
		}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		pool = Pool()
		init()
	}
}
