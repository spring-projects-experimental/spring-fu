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

package org.springframework.fu.kofu.web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.getBean
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory
import org.springframework.fu.kofu.mongo.mongodb
import org.springframework.fu.kofu.webApplication
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.test

/**
 * @author Alexey Nesterov
 */
abstract class AbstractWebServerDslTests(protected val port: Int = 8080) {

	abstract fun getServerFactory(): ConfigurableReactiveWebServerFactory

	@Test
	fun `Create an application with an empty server`() {
		val app = webApplication {
			server {
				engine = getServerFactory()
			}
		}
		with(app.run()){
			close()
		}
	}

	@Test
	fun `Create an application with a server and a filter`() {
		val app = webApplication {
			server {
				engine = getServerFactory()
				filter<MyFilter>()
			}
		}
		with(app.run()){
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$port").build()
			client.get().uri("/foo").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isEqualTo(UNAUTHORIZED)
			close()
		}
	}

	@Test
	fun `Create and request an endpoint`() {
		val app = webApplication {
			server {
				engine = getServerFactory()
				router {
					GET("/foo") { noContent().build() }
				}
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$port").build()
			client.get().uri("/foo").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful
			close()
		}
	}

	@Test
	fun `Create a WebClient and request an endpoint`() {
		val app = webApplication {
			server {
				engine = getServerFactory()
				router {
					GET("/") { noContent().build() }
				}
			}
			client {
				baseUrl = "http://127.0.0.1:$port"
			}
		}
		with(app.run()) {
			val client = getBean<WebClient.Builder>().build()
			client.get().uri("/").exchange().test()
					.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
					.verifyComplete()
			close()
		}
	}

	@Test
	fun `Declare 2 router blocks`() {
		val app = webApplication {
			server {
				engine = getServerFactory()
				router {
					GET("/foo") { noContent().build() }
				}
				router {
					GET("/bar") { ok().build() }
				}
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$port").build()
			client.get().uri("/foo").exchange().expectStatus().isNoContent
			client.get().uri("/bar").exchange().expectStatus().isOk
			close()
		}
	}

	@Test
	fun `Declare 2 server blocks`() {
		val app = webApplication {
			server {
				engine = getServerFactory()

			}
			server {
				engine = getServerFactory()
				port = 8181
			}
		}

		assertThrows<IllegalStateException> {
			app.run()
		}
	}

	@Test
	fun `Check that ConcurrentModificationException is not thrown`() {
		val app = webApplication {
			server {
				engine = getServerFactory()
				codecs {
					string()
					jackson()
				}
				router {
					GET("/") { noContent().build() }
				}
			}
			logging {
				level = LogLevel.DEBUG
			}
			mongodb {
				embedded()
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$port").build()
			client.get().uri("/").exchange().expectStatus().is2xxSuccessful
			close()
		}
	}

	@Test
	fun `run an application 2 times`() {
		val app = webApplication {
			server {
				engine = getServerFactory()
			}
		}
		var context = app.run()
		context.close()
		context = app.run()
		context.close()
	}

	class MyFilter : WebFilter {
		override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
			exchange.response.statusCode = UNAUTHORIZED
			return Mono.empty()
		}
	}

}