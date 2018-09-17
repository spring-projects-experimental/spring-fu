/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.kofu.web

import org.springframework.boot.kofu.AbstractDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * Kofu DSL for WebFlux CORS configuration.
 *
 * @author Ireneusz Kozłowski
 * @author Sebastien Deleuze
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

	/**
	 * Define a CORS configuration mapped to this path via a [dedicated DSL][CorsConfigurationDsl].
	 * @receiver the path to map the [CORS configuration][CorsConfigurationDsl]
	 */
	operator fun String.invoke(defaults: Boolean = this@CorsDsl.defaults, init: CorsConfigurationDsl.() -> Unit = {}) {
		val corsConfigurationDsl = CorsConfigurationDsl(defaults)
		corsConfigurationDsl.init()
		configuration.registerCorsConfiguration(this, corsConfigurationDsl.corsConfiguration)
	}
}

/**
 * Configure CORS via a [dedicated DSL][CorsDsl].
 *
 * Exact path mapping URIs (such as `/admin`) are supported
 * as well as Ant-style path patterns.
 *
 * @sample org.springframework.boot.kofu.samples.corsDsl
 */
fun WebFluxServerDsl.cors(defaults: Boolean = true,
						  init: CorsDsl.() -> Unit = {}) {
	initializers.add(CorsDsl(defaults, init))
}