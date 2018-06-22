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

package org.springframework.fu.module.webflux.jackson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.fu.application
import org.springframework.fu.module.webflux.netty.netty

import org.springframework.fu.module.webflux.webflux
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.test

/**
 * @author Sebastien Deleuze
 */
class JacksonModuleTests {

	@Test
	fun `Enable jackson module on server, create and request a JSON endpoint`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(netty()) {
					codecs {
						jackson()
					}
					routes {
						GET("/user") {
							ok().header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE).syncBody(User("Brian"))
						}
					}
				}
			}
		}
		app.run(context)
		val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()
		client.get().uri("/user").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType(APPLICATION_JSON_UTF8_VALUE)
				.expectBody<User>()
				.isEqualTo(User("Brian"))
		context.close()
	}

	@Test
	fun `Enable jackson module on client and server, create and request a JSON endpoint`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(netty()) {
					codecs {
						jackson()
					}
					routes {
						GET("/user") {
							ok().header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE).syncBody(User("Brian"))
						}
					}
				}
				client {
					codecs {
						jackson()
					}
				}
			}
		}
		app.run(context)
		val client = context.getBean<WebClient>()
		val exchange = client.get().uri("http://localhost:8080/user").exchange()
		exchange.test()
				.consumeNextWith {
					assertEquals(HttpStatus.OK, it.statusCode())
					assertEquals(APPLICATION_JSON_UTF8, it.headers().contentType().get())
				}
				.verifyComplete()

		context.close()
	}

	data class User(val name: String)

}