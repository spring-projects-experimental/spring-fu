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

package org.springframework.web.server

import kotlinx.coroutines.reactive.awaitFirstOrDefault
import org.springframework.http.server.CoroutinesServerHttpResponse
import org.springframework.http.server.coroutine.CoroutinesServerHttpRequest
import org.springframework.web.server.session.asCoroutineWebSession

class DefaultCoroutinesServerWebExchange(private val exchange: ServerWebExchange) : CoroutinesServerWebExchange {
	override val request: CoroutinesServerHttpRequest
		get() = CoroutinesServerHttpRequest(exchange.request)
	override val response: CoroutinesServerHttpResponse
		get() = CoroutinesServerHttpResponse(exchange.response)

	override suspend fun getSession(): CoroutinesWebSession? =
		exchange.session.awaitFirstOrDefault(null)?.asCoroutineWebSession()

	override fun mutate(): CoroutinesServerWebExchange.Builder =
		DefaultCoroutinesServerWebExchangeBuilder(exchange.mutate())

	override fun extractServerWebExchange(): ServerWebExchange = exchange
}

class DefaultCoroutinesServerWebExchangeBuilder(private val builder: ServerWebExchange.Builder) :
	CoroutinesServerWebExchange.Builder {
	override fun build(): CoroutinesServerWebExchange = CoroutinesServerWebExchange(builder.build())

	override fun request(request: CoroutinesServerHttpRequest): CoroutinesServerWebExchange.Builder = apply {
		builder.request(request.extractServerHttpRequest())
	}
}
