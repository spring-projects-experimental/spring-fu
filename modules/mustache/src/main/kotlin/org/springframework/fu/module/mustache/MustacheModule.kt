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

package org.springframework.fu.module.mustache

import com.samskivert.mustache.Mustache
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.Module
import org.springframework.fu.module.webflux.WebFluxModule

/**
 * @author Sebastien Deleuze
 */
class MustacheModule(private val prefix: String,
					 private val suffix: String,
					 private val f: MustacheViewResolver.() -> Unit) : Module {

	override fun initialize(context : GenericApplicationContext) {
		context.registerBean {
			MustacheResourceTemplateLoader(prefix, suffix).let {
				MustacheViewResolver(Mustache.compiler().withLoader(it)).apply {
					setPrefix(prefix)
					setSuffix(suffix)
					f()
				}
			}
		}
	}
}

fun WebFluxModule.WebFluxServerModule.mustache(prefix: String = "classpath:/templates/",
											suffix: String = ".mustache",
											f: MustacheViewResolver.() -> Unit = {}) : MustacheModule {
	val mustacheDsl = MustacheModule(prefix, suffix, f)
	children.add(mustacheDsl)
	return mustacheDsl
}
