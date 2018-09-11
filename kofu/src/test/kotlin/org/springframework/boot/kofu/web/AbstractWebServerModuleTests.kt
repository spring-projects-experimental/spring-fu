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

package org.springframework.boot.kofu.web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.getBean
import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.WebFluxServerModule
import org.springframework.boot.kofu.web.client
import org.springframework.boot.kofu.web.server
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.test

/**
 * @author Alexey Nesterov
 */
abstract class AbstractWebServerModuleTests {

	abstract fun getWebServerModule(port: Int = 8080): WebFluxServerModule.WebServerModule

	@Test
	fun `Create an application with an empty server`() {
		val app = application {
			server(getWebServerModule())
		}
		with(app){
			run()
			stop()
		}
	}

	@Test
	fun `Create and request an endpoint`() {
		val webServerModule = getWebServerModule()
		val app = application {
			server(webServerModule) {
				router {
					GET("/") { noContent().build() }
				}
			}
		}
		app.run()
		val client = WebTestClient.bindToServer().baseUrl(webServerModule.baseUrl).build()
		client.get().uri("/").exchange().expectStatus().is2xxSuccessful
		app.stop()
	}

	@Test
	fun `Create a WebClient and request an endpoint`() {
		val webServerModule = getWebServerModule()
		val app = application {
			server(webServerModule) {
				router {
					GET("/") { noContent().build() }
				}
			}
			client(webServerModule.baseUrl)
		}
		with(app) {
			run()
			val client = context.getBean<WebClient>()
			client.get().uri("/").exchange().test()
					.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
					.verifyComplete()
			stop()
		}
	}

	@Test
	fun `Create 2 WebClient with different names and request an endpoint`() {
		val webServerModule = getWebServerModule()
		val app = application {
			server(webServerModule) {
				router {
					GET("/") { noContent().build() }
				}
			}
			client(baseUrl = webServerModule.baseUrl, name = "client1")
			client(name = "client2")
		}
		with(app) {
			app.run()
			val client1 = context.getBean<WebClient>("client1")
			client1.get().uri("/").exchange().test()
					.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
					.verifyComplete()
			val client2 = context.getBean<WebClient>("client2")
			client2.get().uri("http://127.0.0.1:8080/").exchange().test()
					.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
					.verifyComplete()
			stop()
		}
	}

	@Test
	open fun `Declare 2 router blocks`() {
		val webServerModule = getWebServerModule()
		val app = application {
			server(webServerModule) {
				router {
					GET("/foo") { noContent().build() }
				}
				router {
					GET("/bar") { ok().build() }
				}
			}
		}
		with(app) {
			run()
			val client = WebTestClient.bindToServer().baseUrl(webServerModule.baseUrl).build()
			client.get().uri("/foo").exchange().expectStatus().isNoContent
			client.get().uri("/bar").exchange().expectStatus().isOk
			stop()
		}
	}

	@Test
	open fun `Declare 2 server blocks`() {
		val webServerModule1 = getWebServerModule()
		val webServerModule2 = getWebServerModule(8181)
		val app = application {
			server(webServerModule1) {
				router {
					GET("/foo") { noContent().build() }
				}
			}
			server(webServerModule2) {
				router {
					GET("/bar") { ok().build() }
				}
			}
		}

		assertThrows<IllegalStateException> {
			app.run()
		}
		app.stop()
	}
}