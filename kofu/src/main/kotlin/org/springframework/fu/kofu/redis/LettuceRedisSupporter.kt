package org.springframework.fu.kofu.redis

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import java.time.Duration

interface LettuceRedisSupporter {

	/**
	 * Configure the lettuce client properties via [dedicated DSL][LettuceDsl].
	 */
	fun lettuce(dsl: LettuceDsl.() -> Unit = {})
}

/**
 * Configure the lettuce client properties.
 *
 * @author Waldemar Panas
 */
class LettuceDsl(private val redisProperties: RedisProperties, private val init: LettuceDsl.() -> Unit) : AbstractDsl() {
	init {
		redisProperties.lettuce.pool = RedisProperties.Pool()
	}

	/**
	 * Configure the shutdown timeout.
	 */
	var shutdownTimeout: Duration?
		get() = redisProperties.lettuce.shutdownTimeout
		set(value) {
			redisProperties.lettuce.shutdownTimeout = value
		}

	/**
	 * Configure the lettuce pool configuration via a [dedicated DSL][PoolDsl].
	 */
	fun pool(dsl: PoolDsl.() -> Unit) {
		PoolDsl(redisProperties.lettuce.pool, dsl).initialize(context)
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
	}
}