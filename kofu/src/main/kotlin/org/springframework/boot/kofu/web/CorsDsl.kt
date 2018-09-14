package org.springframework.boot.kofu.web

import org.springframework.boot.kofu.AbstractDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * @author Ireneusz Kozłowski
 */
class CorsDsl(
	private val defaults: Boolean = true,
	private val init: CorsDsl.() -> Unit
) : AbstractDsl() {

	private val configuration = UrlBasedCorsConfigurationSource()

	override fun register(context: GenericApplicationContext) {
		init()
		context.registerBean("corsFilter") {
			CorsWebFilter(configuration)
		}
	}

	operator fun String.invoke(defaults: Boolean = this@CorsDsl.defaults, init: CorsConfigurationDsl.() -> Unit) {
		val corsConfigurationDsl = CorsConfigurationDsl(defaults)
		corsConfigurationDsl.init()
		configuration.registerCorsConfiguration(this, corsConfigurationDsl.corsConfiguration)
	}
}

fun WebFluxServerDsl.cors(
	defaults: Boolean = true,
	init: CorsDsl.() -> Unit = {}) {
	initializers.add(CorsDsl(defaults, init))
}

