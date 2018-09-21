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
import org.springframework.web.function.client.CoroutinesWebClient
import org.springframework.web.function.server.CoroutinesRouterFunctionDsl

class CoroutinesWebFluxClientDsl(private val clientModule: WebFluxClientBuilderDsl) : AbstractDsl() {
	override fun register(context: GenericApplicationContext) {
		context.registerBean {
			if (clientModule.baseUrl != null) {
				CoroutinesWebClient.create(clientModule.baseUrl)
			} else {
				CoroutinesWebClient.create()
			}
		}
	}
}

/**
 * Enable Coroutines support for WebFLux client, registering a [CoroutinesWebClient] bean.
 *
 * @sample org.springframework.boot.kofu.samples.clientCoroutines
 */
fun WebFluxClientBuilderDsl.coroutines()  {
	initializers.add(CoroutinesWebFluxClientDsl(this))
}

/**
 * Define Coroutines router.
 *
 * @sample org.springframework.boot.kofu.samples.coRouterDsl
 */
fun WebFluxServerDsl.coRouter(routes: (CoroutinesRouterFunctionDsl.() -> Unit)) {
	this.include { CoroutinesRouterFunctionDsl(routes).invoke() }
}
