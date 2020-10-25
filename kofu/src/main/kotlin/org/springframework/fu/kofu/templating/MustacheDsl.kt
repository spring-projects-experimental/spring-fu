/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.fu.kofu.templating

import org.springframework.boot.autoconfigure.mustache.MustacheInitializer
import org.springframework.boot.autoconfigure.mustache.MustacheProperties
import org.springframework.boot.autoconfigure.mustache.MustacheReactiveWebInitializer
import org.springframework.boot.autoconfigure.mustache.MustacheServletWebInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.webflux.WebFluxServerDsl
import org.springframework.fu.kofu.webmvc.WebMvcServerDsl

/**
 * Kofu DSL for Mustache template engine.
 *
 * Configure a [Mustache](https://github.com/samskivert/jmustache) view resolver.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-mustache`.
 *
 * @author Sebastien Deleuze
 */
class MustacheDsl(private val init: MustacheDsl.() -> Unit): AbstractDsl() {

	private val properties = MustacheProperties()

	/**
	 * Prefix to apply to template names.
	 */
	var prefix: String
		get() = properties.prefix
		set(value) {
			properties.prefix = value
		}

	var suffix: String
		get() = properties.suffix
		set(value) {
			properties.suffix = value
		}

	fun initializeReactive(context: GenericApplicationContext) {
		this.initialize(context)
		MustacheReactiveWebInitializer(properties).initialize(context)
	}

	fun initializeServlet(context: GenericApplicationContext) {
		this.initialize(context)
		MustacheServletWebInitializer(properties).initialize(context)
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		MustacheInitializer(properties).initialize(context)
	}
}

/**
 * Configure a [Mustache](https://github.com/samskivert/jmustache) view resolver.
 *
 * Require `org.springframework.boot:spring-boot-starter-mustache` dependency.
 *
 * @sample org.springframework.fu.kofu.samples.mustacheDsl
 * @author Sebastien Deleuze
 */
fun WebFluxServerDsl.mustache(dsl: MustacheDsl.() -> Unit = {}) {
	MustacheDsl(dsl).initializeReactive(context)
}
/**
 * Configure a [Mustache](https://github.com/samskivert/jmustache) view resolver.
 *
 * Require `org.springframework.boot:spring-boot-starter-mustache` dependency.
 *
 * @author Sebastien Deleuze
 */
fun WebMvcServerDsl.mustache(dsl: MustacheDsl.() -> Unit = {}) {
	MustacheDsl(dsl).initializeServlet(context)
}
