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

import org.junit.jupiter.api.Test
import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.cors
import org.springframework.boot.kofu.web.netty
import org.springframework.boot.kofu.web.server
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.netty.http.HttpResources

/**
 * @author Ireneusz Kozłowski
 */
class CorsDslTests {

	@Test
	fun `Enable cors module on server, create and request a JSON endpoint`() {
		val app = application {
			server(netty()) {
				cors {
					"/api" {
						allowedOrigins("first.example.com", "second.example.com")
						allowedMethods("GET", "PUT", "POST", "DELETE")
					}
					"/public" {
						allowedOrigins("**")
						allowedMethods("GET")
					}
					"fullConfig" {
						allowedOrigins("full.config.example.com")
						allowedMethods("GET")
						allowedHeaders("*")
						exposedHeaders("Content-Location")
						allowCredentials = true
						maxAge = 3600
						defaults = false
					}
				}
				router {
					GET("/") { noContent().build() }
				}
			}
		}
		with(app) {
			run()
			assert(context.containsBean("corsFilter"))
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:8080").build()
			client.get().uri("/").exchange()
					.expectStatus().is2xxSuccessful
			stop()
			HttpResources.reset() // TODO Possible bug with WebTestClient.bindToServer() + Reactor Netty
		}
	}
}


