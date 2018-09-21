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
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.HttpResources
import reactor.test.test

/**
 * @author Alexey Nesterov
 */
abstract class AbstractWebServerDslTests(protected val port: Int = 8080) {

	abstract fun getServerFactory(): ConfigurableReactiveWebServerFactory

	@Test
	fun `Create an application with an empty server`() {
		val app = application {
			server(getServerFactory())
		}
		with(app){
			run()
			stop()
		}
		HttpResources.reset()
	}

	@Test
	fun `Create and request an endpoint`() {
		val webServerModule = getServerFactory()
		val app = application {
			server(webServerModule) {
				router {
					GET("/foo") { noContent().build() }
				}
			}
		}
		with(app) {
			run()
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$port").build()
			client.get().uri("/foo"). accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful
			stop()
		}
		HttpResources.reset()
	}

	@Test
	fun `Create a WebClient and request an endpoint`() {
		val webServerModule = getServerFactory()
		val app = application {
			server(webServerModule) {
				router {
					GET("/") { noContent().build() }
				}
			}
			client(baseUrl = "http://127.0.0.1:$port")
		}
		with(app) {
			run()
			val client = context.getBean<WebClient.Builder>().build()
			client.get().uri("/").exchange().test()
					.consumeNextWith { assertEquals(NO_CONTENT, it.statusCode()) }
					.verifyComplete()
			stop()
			HttpResources.reset()
		}
	}

	//@Test
	fun `Declare 2 router blocks`() {
		val webServerModule = getServerFactory()
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
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$port").build()
			client.get().uri("/foo").exchange().expectStatus().isNoContent
			client.get().uri("/bar").exchange().expectStatus().isOk
			stop()
		}
		HttpResources.reset()
	}

	@Test
	fun `Declare 2 server blocks`() {
		val webServerModule1 = getServerFactory()
		val webServerModule2 = getServerFactory()
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
		HttpResources.reset()
	}
}