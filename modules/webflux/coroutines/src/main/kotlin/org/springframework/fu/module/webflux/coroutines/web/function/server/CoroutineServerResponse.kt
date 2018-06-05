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

import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.reactive.asPublisher
import kotlinx.coroutines.experimental.reactive.awaitFirst
import kotlinx.coroutines.experimental.reactive.awaitFirstOrDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.fu.module.webflux.coroutines.http.server.CoroutineServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.fu.module.webflux.coroutines.web.function.CoroutineBodyInserter
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.server.RenderingResponse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

interface CoroutineServerResponse {
    fun extractServerResponse(): ServerResponse

    companion object {
        operator fun invoke(resp: ServerResponse): CoroutineServerResponse = DefaultCoroutineServerResponse(resp)

        fun created(location: URI): CoroutineBodyBuilder = CoroutineBodyBuilder(ServerResponse.created(location))

        fun ok(): CoroutineBodyBuilder = status(HttpStatus.OK)

        fun permanentRedirect(location: URI): CoroutineBodyBuilder = ServerResponse.permanentRedirect(location).asCoroutineBodyBuilder()

        fun seeOther(location: URI): CoroutineBodyBuilder =
            ServerResponse.seeOther(location).asCoroutineBodyBuilder()

        fun status(status: HttpStatus): CoroutineBodyBuilder = ServerResponse.status(status).asCoroutineBodyBuilder()
    }
}

interface CoroutineHeadersBuilder {
    fun location(location: URI): CoroutineHeadersBuilder

	fun header(headerName: String, vararg headerValues: String): CoroutineHeadersBuilder
}

interface CoroutineBodyBuilder: CoroutineHeadersBuilder {
    suspend fun build(): CoroutineServerResponse

    suspend fun body(inserter: CoroutineBodyInserter<*, in CoroutineServerHttpResponse>): CoroutineServerResponse?

    suspend fun <T> body(value: T?, elementClass: Class<T>): CoroutineServerResponse?

    suspend fun <T> body(channel: ReceiveChannel<T>, elementClass: Class<T>): CoroutineServerResponse?

    fun contentType(contentType: MediaType): CoroutineBodyBuilder

    suspend fun render(name: String, vararg modelAttributes: Any): CoroutineServerResponse?

    suspend fun render(name: String, model: Map<String, *>): CoroutineServerResponse?

    suspend fun syncBody(body: Any): CoroutineServerResponse?

    companion object {
        operator fun invoke(builder: ServerResponse.BodyBuilder): CoroutineBodyBuilder = DefaultCoroutineBodyBuilder(builder)
    }
}

internal open class DefaultCoroutineServerResponse(val resp: ServerResponse): CoroutineServerResponse {
    override fun extractServerResponse(): ServerResponse = resp
}

internal open class DefaultCoroutineHeadersBuilder<T: ServerResponse.HeadersBuilder<T>>(var builder: T): CoroutineHeadersBuilder {
    override fun location(location: URI): CoroutineHeadersBuilder = apply {
        builder.location(location)
    }

	override fun header(headerName: String, vararg headerValues: String) = apply {
		builder.header(headerName, *headerValues)
	}
}

internal open class DefaultCoroutineBodyBuilder(builder: ServerResponse.BodyBuilder): DefaultCoroutineHeadersBuilder<ServerResponse.BodyBuilder>(builder), CoroutineBodyBuilder {
    override suspend fun build(): CoroutineServerResponse = builder.build().asCoroutineServerResponse()

    suspend override fun body(inserter: CoroutineBodyInserter<*, in CoroutineServerHttpResponse>): CoroutineServerResponse? =
            builder.body(inserter.asBodyInserter()).asCoroutineServerResponse()

    suspend override fun <T> body(value: T?, elementClass: Class<T>): CoroutineServerResponse? =
            builder.body(Mono.justOrEmpty(value), elementClass as Class<T?>).asCoroutineServerResponse()

    suspend override fun <T> body(channel: ReceiveChannel<T>, elementClass: Class<T>): CoroutineServerResponse? =
            builder.body(channel.asPublisher(Unconfined), elementClass).asCoroutineServerResponse()

    override fun contentType(contentType: MediaType): CoroutineBodyBuilder = apply {
        builder.contentType(contentType)
    }

    suspend override fun render(name: String, vararg modelAttributes: Any): CoroutineServerResponse? =
            builder.render(name, modelAttributes).awaitFirst().asCoroutineServerResponse()

    suspend override fun render(name: String, model: Map<String, *>): CoroutineServerResponse? =
            builder.render(name, model).awaitFirst().asCoroutineServerResponse()

    suspend override fun syncBody(body: Any): CoroutineServerResponse? =
            builder.syncBody(body).asCoroutineServerResponse()

    suspend fun Mono<ServerResponse>.asCoroutineServerResponse(): CoroutineServerResponse =
			awaitFirst().let { CoroutineServerResponse(it) }
}

internal open class DefaultCoroutineRenderingResponse(resp: RenderingResponse): DefaultCoroutineServerResponse(resp), CoroutineRenderingResponse {
}

class DefaultCoroutineRenderingResponseBuilder(val builder: RenderingResponse.Builder): CoroutineRenderingResponse.Builder {
    suspend override fun build(): CoroutineRenderingResponse =
            CoroutineRenderingResponse(builder.build().awaitFirst())

    override fun modelAttributes(attributes: Map<String, *>): CoroutineRenderingResponse.Builder = apply {
        builder.modelAttributes(attributes)
    }
}

private fun CoroutineBodyInserter<*, in CoroutineServerHttpResponse>.asBodyInserter(): BodyInserter<Any, ServerHttpResponse> = TODO()

private fun ServerResponse.BodyBuilder.asCoroutineBodyBuilder(): CoroutineBodyBuilder = CoroutineBodyBuilder(this)

inline suspend fun <reified T : Any> CoroutineBodyBuilder.body(channel: ReceiveChannel<T>): CoroutineServerResponse? =
        body(channel, T::class.java)

suspend inline fun <reified T: Any> CoroutineBodyBuilder.body(value: T?): CoroutineServerResponse? =
        body(value, T::class.java)

inline fun <T : CoroutineServerResponse> ServerResponse.asCoroutineServerResponse(): T =
        when (this) {
            is RenderingResponse -> CoroutineRenderingResponse(this)
            else                 -> CoroutineServerResponse(this)
        } as T