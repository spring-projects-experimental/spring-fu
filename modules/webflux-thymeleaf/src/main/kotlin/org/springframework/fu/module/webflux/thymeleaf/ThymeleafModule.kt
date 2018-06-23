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

package org.springframework.fu.module.webflux.thymeleaf

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.Ordered
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxModule
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver


/**
 * @author Artem Gavrilov
 * TODO Support other spring.thymeleaf.* Boot properties
 */
class ThymeleafModule(
		private val cache: Boolean,
		private val suffix: String,
		private val prefix: String) : AbstractModule() {

	private val engine = SpringWebFluxTemplateEngine()
	private val thymeleafUrlTemplateResolver = ThymeleafUrlTemplateResolver()
	private val springResourceTemplateResolver = SpringResourceTemplateResolver()


	override fun initialize(context: GenericApplicationContext) {
		context.registerBean {
			thymeleafUrlTemplateResolver.also {
				it.applicationContext = context
				it.setSuffix(suffix)
				it.setPrefix(prefix)
				it.setViewClass(ThymeleafView::class.java)
				it.order = Ordered.HIGHEST_PRECEDENCE + 5
			}
		}
		context.registerBean {
			springResourceTemplateResolver.also {
				it.setApplicationContext(context)
				it.isCacheable = cache
			}
		}
		context.registerBean {
			engine.also {
				it.templateResolvers = setOf(thymeleafUrlTemplateResolver, springResourceTemplateResolver)
			}
		}

	}
}

fun WebFluxModule.WebFluxServerModule.thymeleaf(
		cache: Boolean = true,
		suffix: String = ".html",
		prefix: String = "classpath:/templates/"): ThymeleafModule {
	val thymeleafDsl = ThymeleafModule(cache, suffix, prefix)
	initializers.add(thymeleafDsl)
	return thymeleafDsl
}