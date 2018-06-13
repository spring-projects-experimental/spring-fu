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

package org.springframework.fu.module.webflux.coroutine.web.function.server

import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.reactive.asPublisher
import kotlinx.coroutines.experimental.reactive.awaitFirst
import org.springframework.fu.module.webflux.coroutine.http.server.CoroutineServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.fu.module.webflux.coroutine.web.function.CoroutineBodyInserter
import org.springframework.http.*
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.server.RenderingResponse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI
import java.time.ZonedDateTime

class CoroutineHandler {

	fun from(other: CoroutineServerResponse) =
			ServerResponse.from(other.extractServerResponse()).asCoroutineBodyBuilder()

	fun created(location: URI) =
			ServerResponse.created(location).asCoroutineBodyBuilder()

	fun ok() = ServerResponse.ok().asCoroutineBodyBuilder()

	fun noContent(): CoroutineHeadersBuilder =
			ServerResponse.noContent().asCoroutineHeadersBuilder()

	fun accepted() = ServerResponse.accepted().asCoroutineBodyBuilder()

	fun permanentRedirect(location: URI): CoroutineBodyBuilder =
			ServerResponse.permanentRedirect(location).asCoroutineBodyBuilder()

	fun temporaryRedirect(location: URI): CoroutineBodyBuilder =
			ServerResponse.temporaryRedirect(location).asCoroutineBodyBuilder()

	fun seeOther(location: URI): CoroutineBodyBuilder =
			ServerResponse.seeOther(location).asCoroutineBodyBuilder()

	fun badRequest() = ServerResponse.badRequest().asCoroutineBodyBuilder()

	fun notFound(): CoroutineHeadersBuilder =
			ServerResponse.notFound().asCoroutineHeadersBuilder()

	fun unprocessableEntity() = ServerResponse.unprocessableEntity().asCoroutineBodyBuilder()

	fun status(status: HttpStatus): CoroutineBodyBuilder = ServerResponse.status(status).asCoroutineBodyBuilder()

	fun status(status: Int) = ServerResponse.status(status).asCoroutineBodyBuilder()

}

suspend fun coroutineHandler(init: suspend CoroutineHandler.() -> CoroutineServerResponse) = CoroutineHandler().init()

interface CoroutineServerResponse {
	fun extractServerResponse(): ServerResponse

	companion object {

		operator fun invoke(resp: ServerResponse): CoroutineServerResponse =
				DefaultCoroutineServerResponse(resp)
	}
}

interface CoroutineHeadersBuilder {

	fun cookie(cookie: ResponseCookie): CoroutineHeadersBuilder

	fun header(headerName: String, vararg headerValues: String): CoroutineHeadersBuilder

	fun headers(headersConsumer: (HttpHeaders) -> Unit): CoroutineHeadersBuilder

	fun cookies(cookiesConsumer: (MultiValueMap<String, ResponseCookie>) -> Unit): CoroutineHeadersBuilder

	fun allow(vararg allowedMethods: HttpMethod): CoroutineHeadersBuilder

	fun allow(allowedMethods: Set<HttpMethod>): CoroutineHeadersBuilder

	fun eTag(eTag: String): CoroutineHeadersBuilder

	fun lastModified(lastModified: ZonedDateTime): CoroutineHeadersBuilder

	fun location(location: URI): CoroutineHeadersBuilder

	fun cacheControl(cacheControl: CacheControl): CoroutineHeadersBuilder

	fun varyBy(vararg requestHeaders: String): CoroutineHeadersBuilder

	companion object {
		operator fun invoke(builder: ServerResponse.HeadersBuilder<*>): CoroutineHeadersBuilder = DefaultCoroutineHeadersBuilder(builder)
	}

}

interface CoroutineBodyBuilder: CoroutineHeadersBuilder {

	suspend fun build(): CoroutineServerResponse

	suspend fun body(inserter: CoroutineBodyInserter<*, in CoroutineServerHttpResponse>): CoroutineServerResponse

	suspend fun <T> body(value: T?, elementClass: Class<T>): CoroutineServerResponse

	suspend fun <T> body(channel: ReceiveChannel<T>, elementClass: Class<T>): CoroutineServerResponse

	fun contentType(contentType: MediaType): CoroutineBodyBuilder

	suspend fun render(name: String, vararg modelAttributes: Any): CoroutineServerResponse

	suspend fun render(name: String, model: Map<String, *>): CoroutineServerResponse

	suspend fun syncBody(body: Any): CoroutineServerResponse

	companion object {
		operator fun invoke(builder: ServerResponse.BodyBuilder): CoroutineBodyBuilder = DefaultCoroutineBodyBuilder(builder)
	}
}

internal open class DefaultCoroutineServerResponse(private val resp: ServerResponse): CoroutineServerResponse {
	override fun extractServerResponse(): ServerResponse = resp
}

internal open class DefaultCoroutineHeadersBuilder(private val builder: ServerResponse.HeadersBuilder<*>): CoroutineHeadersBuilder {

	override fun cookie(cookie: ResponseCookie) = apply {
		builder.cookie(cookie)
	}

	override fun header(headerName: String, vararg headerValues: String) = apply {
		builder.header(headerName, *headerValues)
	}

	override fun headers(headersConsumer: (HttpHeaders) -> Unit) = apply {
		builder.headers(headersConsumer)
	}

	override fun cookies(cookiesConsumer: (MultiValueMap<String, ResponseCookie>) -> Unit) = apply {
		builder.cookies(cookiesConsumer)
	}

	override fun allow(vararg allowedMethods: HttpMethod) = apply {
		builder.allow(*allowedMethods)
	}

	override fun allow(allowedMethods: Set<HttpMethod>) = apply {
		builder.allow(allowedMethods)
	}

	override fun eTag(eTag: String) = apply {
		builder.eTag(eTag)
	}

	override fun lastModified(lastModified: ZonedDateTime) = apply {
		builder.lastModified(lastModified)
	}

	override fun cacheControl(cacheControl: CacheControl) = apply {
		builder.cacheControl(cacheControl)
	}

	override fun varyBy(vararg requestHeaders: String) = apply {
		builder.varyBy(*requestHeaders)
	}

	override fun location(location: URI): CoroutineHeadersBuilder = apply {
		builder.location(location)
	}
}

internal open class DefaultCoroutineBodyBuilder(private val bodyBuilder: ServerResponse.BodyBuilder): DefaultCoroutineHeadersBuilder(bodyBuilder), CoroutineBodyBuilder {
	override suspend fun build(): CoroutineServerResponse = bodyBuilder.build().asCoroutineServerResponse()

	override suspend fun body(inserter: CoroutineBodyInserter<*, in CoroutineServerHttpResponse>): CoroutineServerResponse =
			bodyBuilder.body(inserter.asBodyInserter()).asCoroutineServerResponse()

	override suspend fun <T> body(value: T?, elementClass: Class<T>): CoroutineServerResponse =
			bodyBuilder.body(Mono.justOrEmpty(value), elementClass).asCoroutineServerResponse()

	override suspend fun <T> body(channel: ReceiveChannel<T>, elementClass: Class<T>): CoroutineServerResponse =
			bodyBuilder.body(channel.asPublisher(Unconfined), elementClass).asCoroutineServerResponse()

	override fun contentType(contentType: MediaType): CoroutineBodyBuilder = apply {
		bodyBuilder.contentType(contentType)
	}

	override suspend fun render(name: String, vararg modelAttributes: Any): CoroutineServerResponse =
			bodyBuilder.render(name, modelAttributes).awaitFirst().asCoroutineServerResponse()

	override suspend fun render(name: String, model: Map<String, *>): CoroutineServerResponse =
			bodyBuilder.render(name, model).awaitFirst().asCoroutineServerResponse()

	override suspend fun syncBody(body: Any): CoroutineServerResponse =
			bodyBuilder.syncBody(body).asCoroutineServerResponse()

	suspend fun Mono<ServerResponse>.asCoroutineServerResponse(): CoroutineServerResponse =
			awaitFirst().let { CoroutineServerResponse(it) }
}

internal open class DefaultCoroutineRenderingResponse(resp: RenderingResponse): DefaultCoroutineServerResponse(resp), CoroutineRenderingResponse {
}

class DefaultCoroutineRenderingResponseBuilder(private val builder: RenderingResponse.Builder): CoroutineRenderingResponse.Builder {
	override suspend fun build(): CoroutineRenderingResponse =
			CoroutineRenderingResponse(builder.build().awaitFirst())

	override fun modelAttributes(attributes: Map<String, *>): CoroutineRenderingResponse.Builder = apply {
		builder.modelAttributes(attributes)
	}
}

private fun CoroutineBodyInserter<*, in CoroutineServerHttpResponse>.asBodyInserter(): BodyInserter<Any, ServerHttpResponse> = TODO()

private fun ServerResponse.BodyBuilder.asCoroutineBodyBuilder(): CoroutineBodyBuilder = CoroutineBodyBuilder(this)

private fun ServerResponse.HeadersBuilder<*>.asCoroutineHeadersBuilder(): CoroutineHeadersBuilder = CoroutineHeadersBuilder(this)

suspend inline fun <reified T : Any> CoroutineBodyBuilder.body(channel: ReceiveChannel<T>): CoroutineServerResponse =
		body(channel, T::class.java)

suspend inline fun <reified T: Any> CoroutineBodyBuilder.body(value: T?): CoroutineServerResponse =
		body(value, T::class.java)

fun ServerResponse.asCoroutineServerResponse(): CoroutineServerResponse =
		when (this) {
			is RenderingResponse -> CoroutineRenderingResponse(this)
			else                 -> CoroutineServerResponse(this)
		}