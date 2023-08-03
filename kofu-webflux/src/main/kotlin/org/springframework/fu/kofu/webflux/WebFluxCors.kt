package org.springframework.fu.kofu.webflux

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.kofu.web.AbstractCorsDsl
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * Kofu DSL for WebMvc CORS configuration.
 *
 * @author Sebastien Deleuze
 */
class WebFluxCorsDsl(
		defaults: Boolean = true,
		private val init: AbstractCorsDsl.() -> Unit
) : AbstractCorsDsl(defaults) {

	private val source = UrlBasedCorsConfigurationSource()

	override fun registerCorsConfiguration(path: String, configuration: CorsConfiguration) {
		source.registerCorsConfiguration(path, configuration)
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		context.registerBean("corsFilter") {
			CorsWebFilter(source)
		}
	}
}

/**
 * Configure CORS via a [dedicated DSL][AbstractCorsDsl].
 *
 * Exact path mapping URIs (such as `/admin`) are supported
 * as well as Ant-style path patterns.
 *
 * @param defaults Apply permit default values when set to true (enabled by default)
 * @param dsl Cors DSL
 * @sample org.springframework.fu.kofu.samples.corsDsl
 */
fun WebFluxServerDsl.cors(defaults: Boolean = true,
						  dsl: AbstractCorsDsl.() -> Unit = {}) {
	WebFluxCorsDsl(defaults, dsl).initialize(context)
}
