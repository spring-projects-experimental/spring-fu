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

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeanProvider
import org.springframework.boot.kofu.application
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.HttpResources
import reactor.test.test

/**
 * @author Sebastien Deleuze
 */
class JacksonDslTests {

	@Test
	fun `Enable jackson module on server, create and request a JSON endpoint`() {
		val app = application {
			server {
				codecs {
					jackson()
				}
				router {
					GET("/user") {
						ok().header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE).syncBody(User("Brian"))
					}
				}
			}
		}
		app.run()
		val client = WebTestClient.bindToServer().baseUrl("http://127.0.1:8080").build()
		client.get().uri("/user").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType(APPLICATION_JSON_UTF8_VALUE)
				.expectBody<User>()
				.isEqualTo(User("Brian"))
		app.stop()
		HttpResources.reset()
	}

	@Test
	fun `Enable jackson module on client and server, create and request a JSON endpoint`() {
		val app = application {
			server {
				codecs {
					jackson()
				}
				router {
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
		with(app) {
			run()
			val client = context.getBean<WebClient.Builder>().build()
			val exchange = client.get().uri("http://127.0.1:8080/user").exchange()
			exchange.test()
					.consumeNextWith {
						assertEquals(HttpStatus.OK, it.statusCode())
						assertEquals(APPLICATION_JSON_UTF8, it.headers().contentType().get())
					}
					.verifyComplete()
			val mappers = context.getBeanProvider<ObjectMapper>().toList()
			assertEquals(1, mappers.size)
			stop()
			HttpResources.reset()
		}
	}

	data class User(val name: String)

}