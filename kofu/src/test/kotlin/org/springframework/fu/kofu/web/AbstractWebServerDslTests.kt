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
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.embedded
import org.springframework.fu.kofu.mongo.mongodb
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.router
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
			server {
				engine = getServerFactory()
			}
		}
		with(app.run()){
			close()
		}
	}

	@Test
	fun `Create and request an endpoint`() {
		val router = router {
			GET("/foo") { noContent().build() }
		}
		val app = application {
			server {
				engine = getServerFactory()
				import(router)
			}
		}
		with(app.run()) {
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$port").build()
			client.get().uri("/foo"). accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful
			close()
		}
	}

	@Test
	fun `Create a WebClient and request an endpoint`() {
		val router = router {
			GET("/") { noContent().build() }
		}
		val app = application {
			server {
				engine = getServerFactory()
				import(router)
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
		val router1 = router {
			GET("/foo") { noContent().build() }
		}
		val router2 = router {
			GET("/bar") { ok().build() }
		}

		val app = application {
			server {
				engine = getServerFactory()
				import(router1)
				import(router2)
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
		val app = application {
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
		val router = router {
			GET("/") { noContent().build() }
		}
		val app = application {
			server {
				engine = getServerFactory()
				codecs {
					string()
					jackson()
				}
				import(router)
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
		val app = application {
			server {
				engine = getServerFactory()
			}
		}
		var context = app.run()
		context.close()
		context = app.run()
		context.close()
	}

}