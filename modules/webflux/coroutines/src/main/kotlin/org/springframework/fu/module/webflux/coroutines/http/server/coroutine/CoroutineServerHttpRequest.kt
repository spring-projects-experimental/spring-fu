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

package org.springframework.fu.module.webflux.coroutines.http.server.coroutine

import org.springframework.fu.module.webflux.coroutines.http.CoroutineHttpInputMessage
import org.springframework.http.HttpRequest
import org.springframework.http.server.reactive.ServerHttpRequest

interface CoroutineServerHttpRequest: CoroutineHttpInputMessage, HttpRequest {
    fun mutate(): Builder

    fun extractServerHttpRequest(): ServerHttpRequest

    companion object {
        operator fun invoke(request: ServerHttpRequest): CoroutineServerHttpRequest = DefaultCoroutineServerHttpRequest(request)
    }

    interface Builder {
        fun header(key: String, value: String): Builder

        fun path(path: String): Builder

        fun build(): CoroutineServerHttpRequest
    }
}

class DefaultCoroutineServerHttpRequestBuilder(val builder: ServerHttpRequest.Builder) : CoroutineServerHttpRequest.Builder {
    override fun header(key: String, value: String): CoroutineServerHttpRequest.Builder = apply {
        builder.header(key, value)
    }

    override fun path(path: String): CoroutineServerHttpRequest.Builder = apply {
        builder.path(path)
    }

    override fun build(): CoroutineServerHttpRequest = CoroutineServerHttpRequest(builder.build())
}
