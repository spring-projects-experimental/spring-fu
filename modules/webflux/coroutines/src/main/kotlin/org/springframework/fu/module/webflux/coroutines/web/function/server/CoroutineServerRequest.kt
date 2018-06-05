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

package org.springframework.fu.module.webflux.coroutines.web.function.server

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.reactive.awaitFirstOrDefault
import kotlinx.coroutines.experimental.reactive.openSubscription
import org.springframework.fu.module.webflux.coroutines.http.server.coroutine.CoroutineServerHttpRequest
import org.springframework.fu.module.webflux.coroutines.web.server.CoroutineWebSession
import org.springframework.fu.module.webflux.coroutines.web.function.CoroutineBodyExtractor
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.fu.module.webflux.coroutines.web.server.session.asCoroutineWebSession
import java.net.URI

interface CoroutineServerRequest {
    fun <T> body(extractor: CoroutineBodyExtractor<T, CoroutineServerHttpRequest>): T

    fun <T> body(extractor: CoroutineBodyExtractor<T, CoroutineServerHttpRequest>, hints: Map<String, Any>): T

    suspend fun <T> body(elementClass: Class<out T>): T?

    fun <T> bodyToReceiveChannel(elementClass: Class<out T>): ReceiveChannel<T>

    fun headers(): ServerRequest.Headers

    fun pathVariable(name: String): String?

    suspend fun session(): CoroutineWebSession?

    fun uri(): URI

    fun extractServerRequest(): ServerRequest

    companion object {
        operator fun invoke(req: ServerRequest) = DefaultCoroutineServerRequest(req)
    }
}

class DefaultCoroutineServerRequest(val req: ServerRequest): CoroutineServerRequest {
    override fun <T> body(extractor: CoroutineBodyExtractor<T, CoroutineServerHttpRequest>): T =
            req.body(extractor.asBodyExtractor())

    override fun <T> body(extractor: CoroutineBodyExtractor<T, CoroutineServerHttpRequest>, hints: Map<String, Any>): T =
            req.body(extractor.asBodyExtractor(), hints)

    suspend override fun <T> body(elementClass: Class<out T>): T? =
            req.bodyToMono(elementClass).awaitFirstOrDefault(null)

    override fun <T> bodyToReceiveChannel(elementClass: Class<out T>): ReceiveChannel<T> =
            req.bodyToFlux(elementClass).openSubscription()

    override fun headers(): ServerRequest.Headers = req.headers()

    override fun pathVariable(name: String): String? = req.pathVariable(name)

    suspend override fun session(): CoroutineWebSession? =  req.session().awaitFirstOrDefault(null)?.asCoroutineWebSession()

    override fun uri(): URI = req.uri()

    override fun extractServerRequest(): ServerRequest = req
}

//fun CoroutineServerRequest.language() = TODO()
        //TODO findByTag(this.headers().asHttpHeaders().acceptLanguageAsLocales.first().language)

inline suspend fun <reified T : Any> CoroutineServerRequest.body(): T? = body(T::class.java)


