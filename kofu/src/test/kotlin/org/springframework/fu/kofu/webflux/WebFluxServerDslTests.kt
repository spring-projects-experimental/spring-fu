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

package org.springframework.fu.kofu.webflux

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.getBean
import org.springframework.boot.logging.LogLevel
import org.springframework.fu.kofu.localServerPort
import org.springframework.fu.kofu.mongo.reactiveMongodb
import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

/**
 * @author Alexey Nesterov
 * @author Sebastien Deleuze
 */
class WebFluxServerDslTests {

	@Test
	fun `Create an application with an empty server`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
			}
		}
		with(app.run()){
			close()
		}
	}

	@Test
	fun `Create an application with a server and a filter`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
				filter<MyFilter>()
			}
		}
		with(app.run()){
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
			client.get().uri("/foo").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isEqualTo(UNAUTHORIZED)
			close()
		}
	}

	@Test
	fun `Create and request an endpoint`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
				router {
					GET("/foo") { noContent().build() }
				}
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
			client.get().uri("/foo").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful
			close()
		}
	}

	@Test
	fun `Create and request an endpoint with a customized engine`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
				router {
					engine = tomcat()
					GET("/foo") { noContent().build() }
				}
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
			client.get().uri("/foo").accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful
			close()
		}
	}

	@Test
	fun `Create a WebClient and request an endpoint`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
				router {
					GET("/") { noContent().build() }
				}
			}
			webClient()
		}
		with(app.run()) {
			val client = getBean<WebClient.Builder>().build()
			client.get().uri("http://127.0.0.1:$localServerPort/").exchangeToMono {
				assertEquals(NO_CONTENT, it.statusCode())
				Mono.empty<Void>()
			}.block()
			close()
		}
	}

	@Test
	fun `Declare 2 router blocks`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
				router {
					GET("/foo") { noContent().build() }
				}
				router {
					GET("/bar") { ok().build() }
				}
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
			client.get().uri("/foo").exchange().expectStatus().isNoContent
			client.get().uri("/bar").exchange().expectStatus().isOk
			close()
		}
	}

	@Test
	fun `Declare 2 server blocks`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
			}
			webFlux {
				port = 0
			}
		}

		assertThrows<IllegalStateException> {
			app.run()
		}
	}

	@Test
	fun `Check that ConcurrentModificationException is not thrown`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
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
			reactiveMongodb {
				embedded()
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
			client.get().uri("/").exchange().expectStatus().is2xxSuccessful
			close()
		}
	}

	@Test
	fun `run an application 2 times`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
			}
		}
		var context = app.run()
		context.close()
		context = app.run()
		context.close()
	}

	@Test
	fun `Request static file`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
			client.get().uri("/test.txt").exchange().expectBody<String>().isEqualTo("Test")
			close()
		}
	}

	class MyFilter : WebFilter {
		override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
			exchange.response.statusCode = UNAUTHORIZED
			return Mono.empty()
		}
	}

}