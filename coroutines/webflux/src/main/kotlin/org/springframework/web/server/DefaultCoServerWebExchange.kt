/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.server

import kotlinx.coroutines.reactive.awaitFirstOrDefault
import org.springframework.http.server.CoServerHttpResponse
import org.springframework.http.server.coroutines.CoServerHttpRequest
import org.springframework.web.server.session.asCoroutine

class DefaultCoServerWebExchange(private val exchange: ServerWebExchange) : CoServerWebExchange {
	override val request: CoServerHttpRequest
		get() = CoServerHttpRequest(exchange.request)
	override val response: CoServerHttpResponse
		get() = CoServerHttpResponse(exchange.response)

	override suspend fun getSession(): CoWebSession? =
		exchange.session.awaitFirstOrDefault(null)?.asCoroutine()

	override fun mutate(): CoServerWebExchange.Builder =
		DefaultCoServerWebExchangeBuilder(exchange.mutate())

	override fun extractServerWebExchange(): ServerWebExchange = exchange
}

class DefaultCoServerWebExchangeBuilder(private val builder: ServerWebExchange.Builder) :
	CoServerWebExchange.Builder {
	override fun build(): CoServerWebExchange = CoServerWebExchange(builder.build())

	override fun request(request: CoServerHttpRequest): CoServerWebExchange.Builder = apply {
		builder.request(request.extractServerHttpRequest())
	}
}
