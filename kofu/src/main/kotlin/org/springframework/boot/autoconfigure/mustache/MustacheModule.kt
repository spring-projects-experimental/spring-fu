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

package org.springframework.boot.autoconfigure.mustache

import org.springframework.beans.factory.getBean
import org.springframework.boot.AbstractModule
import org.springframework.boot.autoconfigure.web.reactive.WebFluxServerModule
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

/**
 * @author Sebastien Deleuze
 */
internal class MustacheModule(
	private val prefix: String,
	private val suffix: String
) : AbstractModule() {

	override fun initialize(context: GenericApplicationContext) {
		val properties = MustacheProperties()
		properties.prefix = prefix
		properties.suffix = suffix

		val mustacheConfiguration = MustacheAutoConfiguration(properties, context.environment, context)
		context.registerBean {
			mustacheConfiguration.mustacheTemplateLoader()
		}
		context.registerBean {
			mustacheConfiguration.mustacheCompiler(context.getBean())
		}
		val mustacheReactiveConfiguration = MustacheReactiveWebConfiguration(properties)
		context.registerBean {
			mustacheReactiveConfiguration.mustacheViewResolver(context.getBean())
		}
	}
}

fun WebFluxServerModule.mustache(
	prefix: String = "classpath:/templates/",
	suffix: String = ".mustache") {
	initializers.add(MustacheModule(prefix, suffix))
}
