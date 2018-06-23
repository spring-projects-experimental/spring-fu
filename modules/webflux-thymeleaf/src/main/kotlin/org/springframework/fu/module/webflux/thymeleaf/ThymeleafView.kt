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

package org.springframework.fu.module.webflux.thymeleaf

import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.web.reactive.result.view.AbstractUrlBasedView
import org.springframework.web.server.ServerWebExchange
import org.thymeleaf.context.EngineContext
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*

/**
 * @author Artem Gavrilov
 */
class ThymeleafView : AbstractUrlBasedView() {

	override fun checkResourceExists(locale: Locale): Boolean {
		return applicationContext?.getResource(url!!)!!.exists()
	}

	override fun renderInternal(model: MutableMap<String, Any>, mediaType: MediaType?,
								exchange: ServerWebExchange): Mono<Void> {
		val dataBuffer = exchange.response.bufferFactory().allocateBuffer()
		try {
			val engine = applicationContext!!.getBean(SpringWebFluxTemplateEngine::class.java)
			val charset = Optional.ofNullable(mediaType?.charset).orElse(defaultCharset)
			OutputStreamWriter(dataBuffer.asOutputStream(), charset).use { writer ->
				engine.process(url!!, SpringWebFluxContext(exchange, Locale.getDefault(), model), writer)
				writer.flush()
			}
		} catch (ex: Exception) {
			DataBufferUtils.release(dataBuffer)
			return Mono.error(ex)
		}
		return exchange.response.writeWith(Flux.just(dataBuffer))
	}
}