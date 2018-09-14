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

package org.springframework.fu.web.function.client

import kotlinx.coroutines.reactive.awaitFirstOrDefault
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI
import java.nio.charset.Charset
import java.time.ZonedDateTime

interface CoroutinesWebClient {
	fun get(): RequestHeadersUriSpec<*>

	fun head(): RequestHeadersUriSpec<*>

	fun post(): RequestBodyUriSpec

	fun put(): RequestBodyUriSpec

	fun patch(): RequestBodyUriSpec

	fun delete(): RequestHeadersUriSpec<*>

	fun options(): RequestHeadersUriSpec<*>

	fun method(method: HttpMethod): RequestBodyUriSpec

	interface RequestBodyUriSpec: RequestBodySpec, RequestHeadersUriSpec<RequestBodySpec>

	interface RequestBodySpec: RequestHeadersSpec<RequestBodySpec>

	interface RequestHeadersUriSpec<T: RequestHeadersSpec<T>>: UriSpec<T>, RequestHeadersSpec<T>

	interface UriSpec<T: RequestHeadersSpec<T>> {
		fun uri(uri: String, vararg uriVariables: Any): T

		fun uri(uri: String, uriVariables: Map<String, *>): T

		fun uri(uri: URI): T
	}

	interface RequestHeadersSpec<T: RequestHeadersSpec<T>> {
		fun accept(vararg acceptableMediaTypes: MediaType): T

		fun acceptCharset(vararg acceptableCharsets: Charset): T

		fun cookie(name: String, value: String): T

		fun cookies(cookiesConsumer: (MultiValueMap<String, String>) -> Unit): T

		fun ifModifiedSince(ifModifiedSince: ZonedDateTime): T

		fun ifNoneMatch(vararg ifNoneMatches: String): T

		fun header(headerName: String, vararg headerValues: String): T

		fun headers(headersConsumer: (HttpHeaders) -> Unit): T

		fun attribute(name: String, value: Any): T

		fun attributes(attributesConsumer: (Map<String, Any>) -> Unit): T

		suspend fun retrieve(): CoroutineResponseSpec

		suspend fun exchange(): CoroutinesClientResponse?
	}

	interface CoroutineResponseSpec {
		suspend fun <T> body(clazz: Class<T>): T?
	}

	companion object {
		fun create(): CoroutinesWebClient = DefaultCoroutineWebClient(WebClient.create())
		fun create(url: String): CoroutinesWebClient = DefaultCoroutineWebClient(WebClient.create(url))
	}
}

suspend inline fun <reified T : Any> CoroutinesWebClient.CoroutineResponseSpec.body(): T? = body(T::class.java)

open class DefaultCoroutineWebClient(private val client: WebClient) : CoroutinesWebClient {

	override fun get(): CoroutinesWebClient.RequestHeadersUriSpec<*> = request { client.get() }

	override fun head(): CoroutinesWebClient.RequestHeadersUriSpec<*> = request { client.head() }

	override fun post(): CoroutinesWebClient.RequestBodyUriSpec = request { client.post() }

	override fun put(): CoroutinesWebClient.RequestBodyUriSpec = request { client.put() }

	override fun patch(): CoroutinesWebClient.RequestBodyUriSpec = request { client.patch() }

	override fun delete(): CoroutinesWebClient.RequestHeadersUriSpec<*> = request { client.delete() }

	override fun options(): CoroutinesWebClient.RequestHeadersUriSpec<*> = request { client.options() }

	override fun method(method: HttpMethod): CoroutinesWebClient.RequestBodyUriSpec = request { client.method(method) }

	private fun request(f: () -> WebClient.RequestHeadersUriSpec<*>): CoroutinesWebClient.RequestBodyUriSpec =
		DefaultRequestBodyUriSpec(f.invoke() as WebClient.RequestBodyUriSpec)
}

private fun WebClient.ResponseSpec.asCoroutineResponseSpec(): CoroutinesWebClient.CoroutineResponseSpec =
		DefaultCoroutineResponseSpec(this)

open class DefaultCoroutineResponseSpec(
	private val spec: WebClient.ResponseSpec
): CoroutinesWebClient.CoroutineResponseSpec {
	override suspend fun <T> body(clazz: Class<T>): T? =
			spec.bodyToMono(clazz).awaitFirstOrDefault(null)
}

open class DefaultRequestBodyUriSpec(
	private val spec: WebClient.RequestBodyUriSpec
): CoroutinesWebClient.RequestBodyUriSpec {
	override fun uri(uri: String, vararg uriVariables: Any): CoroutinesWebClient.RequestBodySpec = apply {
		spec.uri(uri, *uriVariables)
	}

	override fun uri(uri: String, uriVariables: Map<String, *>): CoroutinesWebClient.RequestBodySpec = apply {
		spec.uri(uri, uriVariables)
	}

	override fun uri(uri: URI): CoroutinesWebClient.RequestBodySpec = apply {
		spec.uri(uri)
	}

	override fun accept(vararg acceptableMediaTypes: MediaType): CoroutinesWebClient.RequestBodySpec = apply {
		spec.accept(*acceptableMediaTypes)
	}

	override fun acceptCharset(vararg acceptableCharsets: Charset): CoroutinesWebClient.RequestBodySpec = apply {
		spec.acceptCharset(*acceptableCharsets)
	}

	override fun cookie(name: String, value: String): CoroutinesWebClient.RequestBodySpec = apply {
		spec.cookie(name, value)
	}

	override fun cookies(cookiesConsumer: (MultiValueMap<String, String>) -> Unit): CoroutinesWebClient.RequestBodySpec = apply {
		spec.cookies(cookiesConsumer)
	}

	override fun ifModifiedSince(ifModifiedSince: ZonedDateTime): CoroutinesWebClient.RequestBodySpec = apply {
		spec.ifModifiedSince(ifModifiedSince)
	}

	override fun ifNoneMatch(vararg ifNoneMatches: String): CoroutinesWebClient.RequestBodySpec = apply {
		spec.ifNoneMatch(*ifNoneMatches)
	}

	override fun header(headerName: String, vararg headerValues: String): CoroutinesWebClient.RequestBodySpec = apply {
		spec.header(headerName, *headerValues)
	}

	override fun headers(headersConsumer: (HttpHeaders) -> Unit): CoroutinesWebClient.RequestBodySpec = apply {
		spec.headers(headersConsumer)
	}

	override fun attribute(name: String, value: Any): CoroutinesWebClient.RequestBodySpec = apply {
		spec.attribute(name, value)
	}

	override fun attributes(attributesConsumer: (Map<String, Any>) -> Unit): CoroutinesWebClient.RequestBodySpec = apply {
		spec.attributes(attributesConsumer)
	}

	override suspend fun exchange(): CoroutinesClientResponse? = spec.exchange().awaitFirstOrDefault(null)?.let {
		DefaultCoroutinesClientResponse(it)
	}

	override suspend fun retrieve(): CoroutinesWebClient.CoroutineResponseSpec =
		spec.retrieve().asCoroutineResponseSpec()
}