/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.fu.module.mustache

import com.samskivert.mustache.Mustache.Compiler
import org.springframework.core.io.Resource
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.web.reactive.result.view.AbstractUrlBasedView
import org.springframework.web.reactive.result.view.View
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.util.*

/**
 * Spring WebFlux [View] using the Mustache template engine.
 *
 * @param compiler The JMustache compiler to be used by this view. Typically this property is not
 * set directly. Instead a single [Compiler] is expected in the Spring
 * application context which is used to compile Mustache templates.
 * @param charset the charset used for reading Mustache template files.
 *
 * @author Brian Clozel
 * @author Sebastien Deleuze
 */
class MustacheView() : AbstractUrlBasedView() {

	lateinit var compiler: Compiler

	var charset: Charset? = null

	override fun checkResourceExists(locale: Locale): Boolean {
		return resolveResource() != null
	}

	override fun renderInternal(
		model: MutableMap<String, Any>, contentType: MediaType?,
		exchange: ServerWebExchange
	): Mono<Void> {
		val resource = resolveResource() ?: return Mono.error(
			IllegalStateException(
				"Could not find Mustache template with URL [$url]"
			)
		)
		val dataBuffer = exchange.response.bufferFactory().allocateBuffer()
		try {
			getReader(resource).use { reader ->
				val template = compiler.compile(reader)
				val charset = getCharset(contentType).orElse(defaultCharset)
				OutputStreamWriter(
					dataBuffer.asOutputStream(),
					charset
				).use { writer ->
					template.execute(model, writer)
					writer.flush()
				}
			}
		} catch (ex: Exception) {
			DataBufferUtils.release(dataBuffer)
			return Mono.error(ex)
		}

		return exchange.response.writeWith(Flux.just(dataBuffer))
	}

	private fun resolveResource(): Resource? {
		val resource = applicationContext!!.getResource(url!!)
		return if (!resource.exists()) {
			null
		} else resource
	}

	private fun getReader(resource: Resource) =
		if (charset != null) InputStreamReader(
			resource.inputStream,
			charset
		) else InputStreamReader(resource.inputStream)

	private fun getCharset(mediaType: MediaType?): Optional<Charset> {
		return Optional.ofNullable(mediaType?.charset)
	}

}