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

import org.springframework.http.server.CoServerHttpResponse
import org.springframework.http.server.coroutines.CoServerHttpRequest

interface CoServerWebExchange {

	val request: CoServerHttpRequest

	val response: CoServerHttpResponse

	suspend fun getSession(): CoWebSession?

	fun mutate(): Builder

	fun extractServerWebExchange(): ServerWebExchange

	companion object {
		operator fun invoke(exchange: ServerWebExchange): CoServerWebExchange =
			DefaultCoServerWebExchange(exchange)
	}

	interface Builder {
		fun request(request: CoServerHttpRequest): Builder

		fun build(): CoServerWebExchange
	}
}