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

package org.springframework.fu.module.mustache

import org.junit.jupiter.api.Test
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.application
import org.springframework.fu.module.webflux.netty.netty


import org.springframework.fu.module.webflux.webflux
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.server.ServerResponse.ok

/**
 * @author Sebastien Deleuze
 */
class MustacheModuleTests {

	@Test
	fun `Create and request a Mustache view`() {
		val context = GenericApplicationContext()
		val app = application {
			webflux {
				server(netty()) {
					mustache()
					routes {
						GET("/view") { ok().render("template", mapOf("name" to "world")) }
					}
				}
			}
		}
		app.run(context)
		val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()
		client.get().uri("/view").exchange()
				.expectStatus().is2xxSuccessful
				.expectBody<String>()
				.isEqualTo("Hello world!")
		context.close()
	}

}