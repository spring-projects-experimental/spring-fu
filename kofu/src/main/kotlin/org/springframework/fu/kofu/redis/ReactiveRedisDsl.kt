package org.springframework.fu.kofu.redis

import org.springframework.boot.autoconfigure.data.redis.*
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for Reactive Redis configuration
 *
 * @author Waldemar Panas
 */
class ReactiveRedisDsl(private val init: ReactiveRedisDsl.() -> Unit) : AbstractRedisDsl(), LettuceRedisSupporter {

	override fun lettuce(dsl: LettuceDsl.() -> Unit) {
		LettuceDsl(properties, dsl).initialize(context)
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		ClusterInitializer(properties.cluster).initialize(context)
		SentinelInitializer(properties.sentinel).initialize(context)
		JedisRedisInitializer(properties).initialize(context)
		LettuceRedisInitializer(properties).initialize(context)
		RedisReactiveInitializer().initialize(context)
	}
}

/**
 * Configure Reactive Redis support
 * @see ReactiveRedisDsl
 */
fun ConfigurationDsl.reactiveRedis(dsl: ReactiveRedisDsl.() -> Unit = {}) {
	ReactiveRedisDsl(dsl).initialize(context)
}