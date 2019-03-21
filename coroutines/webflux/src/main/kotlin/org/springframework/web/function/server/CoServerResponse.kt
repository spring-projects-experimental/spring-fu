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

package org.springframework.web.function.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.*
import org.springframework.http.server.CoServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.util.MultiValueMap
import org.springframework.web.function.CoBodyInserter
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.server.RenderingResponse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI
import java.time.ZonedDateTime

class CoHandlerFunction {

	fun from(other: CoServerResponse) =
		ServerResponse.from(other.extractServerResponse()).asCoroutine()

	fun created(location: URI) =
		ServerResponse.created(location).asCoroutine()

	fun ok() = ServerResponse.ok().asCoroutine()

	fun noContent(): CoHeadersBuilder =
		ServerResponse.noContent().asCoroutine()

	fun accepted() = ServerResponse.accepted().asCoroutine()

	fun permanentRedirect(location: URI): CoBodyBuilder =
		ServerResponse.permanentRedirect(location).asCoroutine()

	fun temporaryRedirect(location: URI): CoBodyBuilder =
		ServerResponse.temporaryRedirect(location).asCoroutine()

	fun seeOther(location: URI): CoBodyBuilder =
		ServerResponse.seeOther(location).asCoroutine()

	fun badRequest() = ServerResponse.badRequest().asCoroutine()

	fun notFound(): CoHeadersBuilder =
		ServerResponse.notFound().asCoroutine()

	fun unprocessableEntity() = ServerResponse.unprocessableEntity().asCoroutine()

	fun status(status: HttpStatus): CoBodyBuilder = ServerResponse.status(status).asCoroutine()

	fun status(status: Int) = ServerResponse.status(status).asCoroutine()

}

suspend fun coHandler(init: suspend CoHandlerFunction.() -> CoServerResponse) =
	CoHandlerFunction().init()

interface CoServerResponse {
	fun extractServerResponse(): ServerResponse

	companion object {

		operator fun invoke(resp: ServerResponse): CoServerResponse =
			DefaultCoServerResponse(resp)
	}
}

interface CoHeadersBuilder {

	fun cookie(cookie: ResponseCookie): CoHeadersBuilder

	fun header(headerName: String, vararg headerValues: String): CoHeadersBuilder

	fun headers(headersConsumer: (HttpHeaders) -> Unit): CoHeadersBuilder

	fun cookies(cookiesConsumer: (MultiValueMap<String, ResponseCookie>) -> Unit): CoHeadersBuilder

	fun allow(vararg allowedMethods: HttpMethod): CoHeadersBuilder

	fun allow(allowedMethods: Set<HttpMethod>): CoHeadersBuilder

	fun eTag(eTag: String): CoHeadersBuilder

	fun lastModified(lastModified: ZonedDateTime): CoHeadersBuilder

	fun location(location: URI): CoHeadersBuilder

	fun cacheControl(cacheControl: CacheControl): CoHeadersBuilder

	fun varyBy(vararg requestHeaders: String): CoHeadersBuilder

	companion object {
		operator fun invoke(builder: ServerResponse.HeadersBuilder<*>): CoHeadersBuilder =
			DefaultCoHeadersBuilder(builder)
	}

}

interface CoBodyBuilder : CoHeadersBuilder {

	suspend fun build(): CoServerResponse

	suspend fun body(inserter: CoBodyInserter<*, in CoServerHttpResponse>): CoServerResponse

	suspend fun <T> body(value: T?, elementClass: Class<T>): CoServerResponse

	suspend fun <T> body(channel: ReceiveChannel<T>, elementClass: Class<T>): CoServerResponse

	fun contentType(contentType: MediaType): CoBodyBuilder

	suspend fun render(name: String, vararg modelAttributes: Any): CoServerResponse

	suspend fun render(name: String, model: Map<String, *>): CoServerResponse

	suspend fun syncBody(body: Any): CoServerResponse

	companion object {
		operator fun invoke(builder: ServerResponse.BodyBuilder): CoBodyBuilder =
			DefaultCoBodyBuilder(builder)
	}
}

internal open class DefaultCoServerResponse(private val resp: ServerResponse) : CoServerResponse {
	override fun extractServerResponse(): ServerResponse = resp
}

internal open class DefaultCoHeadersBuilder(private val builder: ServerResponse.HeadersBuilder<*>) :
	CoHeadersBuilder {

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

	override fun location(location: URI): CoHeadersBuilder = apply {
		builder.location(location)
	}
}

internal open class DefaultCoBodyBuilder(private val bodyBuilder: ServerResponse.BodyBuilder) :
	DefaultCoHeadersBuilder(bodyBuilder), CoBodyBuilder {
	override suspend fun build(): CoServerResponse = bodyBuilder.build().asCoroutine()

	override suspend fun body(inserter: CoBodyInserter<*, in CoServerHttpResponse>): CoServerResponse =
		bodyBuilder.body(inserter.asBodyInserter()).asCoroutine()

	override suspend fun <T> body(value: T?, elementClass: Class<T>): CoServerResponse =
		bodyBuilder.body(Mono.justOrEmpty(value), elementClass).asCoroutine()

	@UseExperimental(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
	override suspend fun <T> body(channel: ReceiveChannel<T>, elementClass: Class<T>): CoServerResponse =
		bodyBuilder.body(channel.asPublisher(Dispatchers.Unconfined), elementClass).asCoroutine()

	override fun contentType(contentType: MediaType): CoBodyBuilder = apply {
		bodyBuilder.contentType(contentType)
	}

	override suspend fun render(name: String, vararg modelAttributes: Any): CoServerResponse =
		bodyBuilder.render(name, modelAttributes).awaitFirst().asCoroutine()

	override suspend fun render(name: String, model: Map<String, *>): CoServerResponse =
		bodyBuilder.render(name, model).awaitFirst().asCoroutine()

	override suspend fun syncBody(body: Any): CoServerResponse =
		bodyBuilder.syncBody(body).asCoroutine()

	suspend fun Mono<ServerResponse>.asCoroutine(): CoServerResponse =
		awaitFirst().let { CoServerResponse(it) }
}

internal open class DefaultCoRenderingResponse(resp: RenderingResponse) : DefaultCoServerResponse(resp),
	CoRenderingResponse {
}

class DefaultCoRenderingResponseBuilder(private val builder: RenderingResponse.Builder) :
	CoRenderingResponse.Builder {
	override suspend fun build(): CoRenderingResponse =
		CoRenderingResponse(builder.build().awaitFirst())

	override fun modelAttributes(attributes: Map<String, *>): CoRenderingResponse.Builder = apply {
		builder.modelAttributes(attributes)
	}
}

private fun CoBodyInserter<*, in CoServerHttpResponse>.asBodyInserter(): BodyInserter<Any, ServerHttpResponse> =
	TODO()

internal fun ServerResponse.BodyBuilder.asCoroutine(): CoBodyBuilder = CoBodyBuilder(this)

internal fun ServerResponse.HeadersBuilder<*>.asCoroutine(): CoHeadersBuilder =
	CoHeadersBuilder(this)

suspend inline fun <reified T : Any> CoBodyBuilder.body(channel: ReceiveChannel<T>): CoServerResponse =
	body(channel, T::class.java)

suspend inline fun <reified T : Any> CoBodyBuilder.body(value: T?): CoServerResponse =
	body(value, T::class.java)

fun ServerResponse.asCoroutine(): CoServerResponse =
	when (this) {
		is RenderingResponse -> CoRenderingResponse(this)
		else -> CoServerResponse(this)
	}