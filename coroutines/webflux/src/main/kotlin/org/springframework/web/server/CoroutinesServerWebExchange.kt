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

import org.springframework.http.server.CoroutinesServerHttpResponse
import org.springframework.http.server.coroutine.CoroutinesServerHttpRequest

interface CoroutinesServerWebExchange {

	val request: CoroutinesServerHttpRequest

	val response: CoroutinesServerHttpResponse

	suspend fun getSession(): CoroutinesWebSession?

	fun mutate(): Builder

	fun extractServerWebExchange(): ServerWebExchange

	companion object {
		operator fun invoke(exchange: ServerWebExchange): CoroutinesServerWebExchange =
			DefaultCoroutinesServerWebExchange(exchange)
	}

	interface Builder {
		fun request(request: CoroutinesServerHttpRequest): Builder

		fun build(): CoroutinesServerWebExchange
	}
}