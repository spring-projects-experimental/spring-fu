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
import com.samskivert.mustache.Mustache.Compiler

import org.springframework.web.reactive.result.view.AbstractUrlBasedView
import org.springframework.web.reactive.result.view.UrlBasedViewResolver
import org.springframework.web.reactive.result.view.ViewResolver
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Spring WebFlux [ViewResolver] for Mustache.
 *
 * @param compiler the Mustache compiler used to compile templates
 * @param charset the charset to use for view rendering
 *
 * @author Brian Clozel
 * @author Sebastien Deleuze
 */
class MustacheViewResolver(
	private val compiler: Compiler = Mustache.compiler(),
	private var charset: Charset = StandardCharsets.UTF_8
) : UrlBasedViewResolver() {

	init {
		viewClass = requiredViewClass()
	}

	override fun requiredViewClass(): Class<*> {
		return MustacheView::class.java
	}

	override fun createView(viewName: String): AbstractUrlBasedView {
		val view = super.createView(viewName) as MustacheView
		view.compiler = compiler
		view.charset = charset
		return view
	}
}