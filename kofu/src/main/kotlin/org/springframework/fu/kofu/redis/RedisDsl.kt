package org.springframework.fu.kofu.redis

import org.springframework.boot.autoconfigure.data.redis.*
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for Redis configuration
 *
 * @author Waldemar Panas
 */
class RedisDsl(private val init: RedisDsl.() -> Unit) : AbstractRedisDsl(), JedisRedisSupporter, LettuceRedisSupporter {

	override fun jedis(dsl: PoolDsl.() -> Unit) {
		properties.jedis.pool = Pool()
		JedisDsl(properties, dsl).initialize(context)
	}

	override fun lettuce(dsl: LettuceDsl.() -> Unit) {
		properties.lettuce.pool = Pool()
		LettuceDsl(properties, dsl).initialize(context)
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		ClusterInitializer(properties.cluster).initialize(context)
		SentinelInitializer(properties.sentinel).initialize(context)
		JedisRedisInitializer(properties).initialize(context)
		LettuceRedisInitializer(properties).initialize(context)
		RedisInitializer().initialize(context)
	}
}


/**
 * Configure Redis support
 * @see RedisDsl
 */
fun ConfigurationDsl.redis(dsl: RedisDsl.() -> Unit = {}) {
	RedisDsl(dsl).initialize(context)
}