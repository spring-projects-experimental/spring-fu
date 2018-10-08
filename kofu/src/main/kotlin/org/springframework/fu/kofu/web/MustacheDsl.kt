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

package org.springframework.fu.kofu.web

import org.springframework.boot.autoconfigure.mustache.MustacheInitializer
import org.springframework.boot.autoconfigure.mustache.MustacheProperties
import org.springframework.boot.autoconfigure.mustache.MustacheReactiveWebInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl

open class MustacheDsl(private val init: MustacheDsl.() -> Unit): AbstractDsl() {

	private val properties = MustacheProperties()

	var prefix: String = "classpath:/templates/"
		set(value) {
			properties.prefix = value
		}

	var suffix: String = ".mustache"
		set(value) {
			properties.suffix = value
		}

	override fun register(context: GenericApplicationContext) {
		init()
		initializers.add(MustacheInitializer(properties))
		initializers.add(MustacheReactiveWebInitializer(properties))
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
	initializers.add(MustacheDsl(dsl))
}
