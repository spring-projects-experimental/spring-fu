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

package org.springframework.fu.kofu.webmvc

import org.junit.jupiter.api.Test
import org.springframework.fu.kofu.localServerPort
import org.springframework.fu.kofu.webApplication
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * @author Sebastien Deleuze
 */
class CorsDslTests {

	@Test
	fun `Enable cors module on server, create and request a JSON endpoint`() {
		val app = webApplication {
			webMvc {
				port = 0
				router {
					GET("/") { noContent().build() }
				}
				cors {
					"/api" {
						allowedOrigins = listOf("first.example.com", "second.example.com")
						allowedMethods = listOf("GET", "PUT", "POST", "DELETE")
					}
					"/public" {
						allowedOrigin = "*"
						allowedMethod = "GET"
					}
					"fullConfig" {
						allowedOrigin = "full.config.example.com"
						allowedMethod = "GET"
						allowedHeader = "*"
						exposedHeader = "Content-Location"
						allowCredentials = true
						maxAge = 3600
					}
				}
			}
		}
		with(app.run()) {
			assert(containsBean("corsFilter"))
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
			client.get().uri("/").exchange().expectStatus().is2xxSuccessful
			close()
		}
	}
}


