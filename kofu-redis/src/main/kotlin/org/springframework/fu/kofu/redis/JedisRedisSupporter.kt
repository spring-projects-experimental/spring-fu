package org.springframework.fu.kofu.redis

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.support.GenericApplicationContext

interface JedisRedisSupporter {
	/**
	 * Configure the jedis client properties via a [dedicated DSL][PoolDsl].
	 */
	fun jedis(dsl: PoolDsl.() -> Unit = {})
}

/**
 * Configure the jedis client properties
 *
 * @author Waldemar Panas
 */
class JedisDsl(redisProperties: RedisProperties, private val init: PoolDsl.() -> Unit) : PoolDsl(redisProperties.jedis.pool, init) {
	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
	}
}
