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

package org.springframework.fu.kofu.webflux

import org.springframework.boot.autoconfigure.mustache.MustacheInitializer
import org.springframework.boot.autoconfigure.mustache.MustacheProperties
import org.springframework.boot.autoconfigure.mustache.MustacheReactiveWebInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl

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
	var prefix: String = MustacheProperties.DEFAULT_PREFIX
		set(value) {
			properties.prefix = value
		}

	var suffix: String = MustacheProperties.DEFAULT_SUFFIX
		set(value) {
			properties.suffix = value
		}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		MustacheInitializer(properties).initialize(context)
		MustacheReactiveWebInitializer(properties).initialize(context)
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
	MustacheDsl(dsl).initialize(context)
}
