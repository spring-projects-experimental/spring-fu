/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.fu.module.webflux.coroutines.web.server

import org.springframework.fu.module.webflux.coroutines.http.server.CoroutineServerHttpResponse
import org.springframework.fu.module.webflux.coroutines.http.server.coroutine.CoroutineServerHttpRequest
import org.springframework.web.server.ServerWebExchange

interface CoroutineServerWebExchange {
    val request: CoroutineServerHttpRequest

    val response: CoroutineServerHttpResponse

    suspend fun getSession(): CoroutineWebSession?

    fun mutate(): Builder

    fun extractServerWebExchange(): ServerWebExchange

    companion object {
        operator fun invoke(exchange: ServerWebExchange): CoroutineServerWebExchange = DefaultCoroutineServerWebExchange(exchange)
    }

    interface Builder {
        fun request(request: CoroutineServerHttpRequest): Builder

        fun build(): CoroutineServerWebExchange
    }
}