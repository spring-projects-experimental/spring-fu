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

package org.springframework.fu.module.webflux.coroutines.web.server

import kotlinx.coroutines.experimental.reactive.awaitFirstOrDefault
import org.springframework.fu.module.webflux.coroutines.http.server.CoroutineServerHttpResponse
import org.springframework.fu.module.webflux.coroutines.http.server.coroutine.CoroutineServerHttpRequest
import org.springframework.fu.module.webflux.coroutines.web.server.session.asCoroutineWebSession
import org.springframework.web.server.ServerWebExchange

class DefaultCoroutineServerWebExchange(val exchange: ServerWebExchange): CoroutineServerWebExchange {
    override val request: CoroutineServerHttpRequest
        get() = CoroutineServerHttpRequest(exchange.request)
    override val response: CoroutineServerHttpResponse
        get() = CoroutineServerHttpResponse(exchange.response)

    override suspend fun getSession(): CoroutineWebSession? = exchange.session.awaitFirstOrDefault(null)?.asCoroutineWebSession()

    override fun mutate(): CoroutineServerWebExchange.Builder = DefaultCoroutineServerWebExchangeBuilder(exchange.mutate())

    override fun extractServerWebExchange(): ServerWebExchange = exchange
}

class DefaultCoroutineServerWebExchangeBuilder(val builder: ServerWebExchange.Builder) : CoroutineServerWebExchange.Builder {
    override fun build(): CoroutineServerWebExchange = CoroutineServerWebExchange(builder.build())

    override fun request(request: CoroutineServerHttpRequest): CoroutineServerWebExchange.Builder = apply {
        builder.request(request.extractServerHttpRequest())
    }
}
