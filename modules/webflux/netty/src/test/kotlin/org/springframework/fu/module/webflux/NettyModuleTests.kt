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

package org.springframework.fu.module.webflux

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.application
import org.springframework.fu.module.webflux.netty.netty
import org.springframework.http.HttpStatus.*
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import reactor.test.test

/**
 * @author Sebastien Deleuze
 */
class NettyModuleTests {

	@Test
	fun `Create an application with an empty server`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(netty())
			}
		}
		app.run(context)
		context.getBean<WebServer>()
		context.close()
	}

	@Test
	fun `Create and request an endpoint`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(netty()) {
					routes {
						GET("/") { noContent().build() }
					}
				}
			}
		}
		app.run(context)
		val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()
		client.get().uri("/").exchange().expectStatus().is2xxSuccessful
		context.close()
	}

	@Test
	fun `Create a WebClient and request an endpoint`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(netty()) {
					routes {
						GET("/") { noContent().build() }
					}
				}
				client("http://localhost:8080")
			}
		}
		app.run(context)
		val client = context.getBean<WebClient>()
		client.get().uri("/").exchange().test()
				.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
				.verifyComplete()
		context.close()
	}

	@Test
	fun `Create 2 WebClient with different names and request an endpoint`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(netty()) {
					routes {
						GET("/") { noContent().build() }
					}
				}
				client(baseUrl = "http://localhost:8080", name = "client1")
				client(name = "client2")
			}
		}
		app.run(context)
		val client1 = context.getBean<WebClient>("client1")
		client1.get().uri("/").exchange().test()
				.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
				.verifyComplete()
		val client2 = context.getBean<WebClient>("client2")
		client2.get().uri("http://localhost:8080/").exchange().test()
				.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
				.verifyComplete()
		context.close()
	}

}