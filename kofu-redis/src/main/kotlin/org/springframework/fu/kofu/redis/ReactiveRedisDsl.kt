package org.springframework.fu.kofu.redis

import org.springframework.boot.autoconfigure.data.redis.ClusterInitializer
import org.springframework.boot.autoconfigure.data.redis.LettuceRedisInitializer
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveInitializer
import org.springframework.boot.autoconfigure.data.redis.SentinelInitializer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for Reactive Redis configuration
 *
 * @author Waldemar Panas
 * @author Sebastien Deleuze
 */
class ReactiveRedisDsl(private val init: ReactiveRedisDsl.() -> Unit) : AbstractRedisDsl(), LettuceRedisSupporter {

	private var lettuceInitializer: ApplicationContextInitializer<GenericApplicationContext>? = null

	override fun lettuce(dsl: LettuceDsl.() -> Unit) {
		lettuceInitializer = ApplicationContextInitializer {
			LettuceDsl(properties, dsl).initialize(it)
			LettuceRedisInitializer(properties).initialize(it)
		}
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		if (lettuceInitializer == null) lettuce()
		lettuceInitializer!!.initialize(context)
		RedisReactiveInitializer().initialize(context)
		ClusterInitializer(properties.cluster).initialize(context)
		SentinelInitializer(properties.sentinel).initialize(context)
	}
}

/**
 * Configure Reactive Redis support by registering 2 beans: `reactiveRedisTemplate` of
 * type `ReactiveRedisTemplate<Any, Any>` and `reactiveStringRedisTemplate` of type
 * `ReactiveStringRedisTemplate`.
 *
 * @see ReactiveRedisDsl
 */
fun ConfigurationDsl.reactiveRedis(dsl: ReactiveRedisDsl.() -> Unit = {}) {
	ReactiveRedisDsl(dsl).initialize(context)
}
