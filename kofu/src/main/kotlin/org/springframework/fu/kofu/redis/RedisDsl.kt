package org.springframework.fu.kofu.redis

import org.springframework.boot.autoconfigure.data.redis.ClusterInitializer
import org.springframework.boot.autoconfigure.data.redis.JedisRedisInitializer
import org.springframework.boot.autoconfigure.data.redis.LettuceRedisInitializer
import org.springframework.boot.autoconfigure.data.redis.RedisInitializer
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool
import org.springframework.boot.autoconfigure.data.redis.SentinelInitializer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for Redis configuration
 *
 * @author Waldemar Panas
 * @author Sebastien Deleuze
 */
@Suppress("UsePropertyAccessSyntax")
class RedisDsl(private val init: RedisDsl.() -> Unit) : AbstractRedisDsl(), JedisRedisSupporter, LettuceRedisSupporter {

	private var jedisInitializer: ApplicationContextInitializer<GenericApplicationContext>? = null

	private var lettuceInitializer: ApplicationContextInitializer<GenericApplicationContext>? = null


	override fun jedis(dsl: PoolDsl.() -> Unit) {
		jedisInitializer = ApplicationContextInitializer {
			JedisDsl(properties, dsl).initialize(it)
			JedisRedisInitializer(properties).initialize(it)
		}
	}

	override fun lettuce(dsl: LettuceDsl.() -> Unit) {
		lettuceInitializer = ApplicationContextInitializer {
			LettuceDsl(properties, dsl).initialize(context)
			LettuceRedisInitializer(properties).initialize(context)
		}
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		if (jedisInitializer != null) {
			jedisInitializer!!.initialize(context)
		}
		else {
			if (lettuceInitializer == null) lettuce()
			lettuceInitializer!!.initialize(context)
		}
		RedisInitializer().initialize(context)
		ClusterInitializer(properties.cluster).initialize(context)
		SentinelInitializer(properties.sentinel).initialize(context)
	}
}


/**
 * Configure Redis support by registering 2 beans: `redisTemplate` of type `RedisTemplate<Any, Any>`
 * and `stringRedisTemplate` of type `StringRedisTemplate`. `jedis()` or `lettuce()` can be used
 * to select the driver, Lettuce will be used by default if none is used.
 * @see RedisDsl
 */
fun ConfigurationDsl.redis(dsl: RedisDsl.() -> Unit = {}) {
	RedisDsl(dsl).initialize(context)
}
