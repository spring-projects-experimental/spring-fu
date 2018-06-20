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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.application
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.test

/**
 * @author Alexey Nesterov
 */
abstract class AbstractWebServerModuleTests {

	abstract fun getWebServerModule(port: Int = 8080, host: String = "127.0.0.1"): WebFluxModule.WebServerModule

	@Test
	fun `Create an application with an empty server`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(getWebServerModule())
			}
		}
		app.run(context)
		context.getBean<WebServer>()
		context.close()
	}

	@Test
	fun `Create and request an endpoint`() {
		val context = GenericApplicationContext()
		val webServerModule = getWebServerModule()
		val app = application {
			webflux {
				server(webServerModule) {
					routes {
						GET("/") { noContent().build() }
					}
				}
			}
		}
		app.run(context)
		val client = WebTestClient.bindToServer().baseUrl(webServerModule.baseUrl).build()
		client.get().uri("/").exchange().expectStatus().is2xxSuccessful
		context.close()
	}

	@Test
	fun `Create a WebClient and request an endpoint`() {
		val context = GenericApplicationContext()
		val webServerModule = getWebServerModule()
		val app = application {
			webflux {
				server(webServerModule) {
					routes {
						GET("/") { noContent().build() }
					}
				}
				client(webServerModule.baseUrl)
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
		val webServerModule = getWebServerModule()
		val app = application {
			webflux {
				server(webServerModule) {
					routes {
						GET("/") { noContent().build() }
					}
				}
				client(baseUrl = webServerModule.baseUrl, name = "client1")
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

	@Test
	open fun `Declare 2 routes blocks`() {
		val context = GenericApplicationContext()
		val webServerModule = getWebServerModule()
		val app = application {
			webflux {
				server(webServerModule) {
					routes {
						GET("/foo") { noContent().build() }
					}
					routes {
						GET("/bar") { ok().build() }
					}
				}
			}
		}
		app.run(context)
		val client = WebTestClient.bindToServer().baseUrl(webServerModule.baseUrl).build()
		client.get().uri("/foo").exchange().expectStatus().isNoContent
		client.get().uri("/bar").exchange().expectStatus().isOk
		context.close()
	}
}