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

package org.springframework.fu.module.webflux.coroutines.http.codec

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import org.springframework.core.ResolvableType
import org.springframework.fu.module.webflux.coroutines.http.server.coroutine.CoroutineServerHttpRequest
import org.springframework.fu.module.webflux.coroutines.http.server.CoroutineServerHttpResponse
import org.springframework.fu.module.webflux.coroutines.http.CoroutineHttpInputMessage
import org.springframework.http.MediaType

interface CoroutineHttpMessageReader<out T> {
    fun canRead(elementType: ResolvableType, mediaType: MediaType): Boolean

    fun read(elementType: ResolvableType, message: CoroutineHttpInputMessage, hints: Map<String, Any>): ReceiveChannel<T>

    suspend fun readSingle(elementType: ResolvableType, message: CoroutineHttpInputMessage, hints: Map<String, Any>): T?

    suspend fun readSingle(actualType: ResolvableType, elementType: ResolvableType, request: CoroutineServerHttpRequest,
						   response: CoroutineServerHttpResponse?, hints: Map<String, Any>): T? =
        readSingle(elementType, request, hints)
}