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

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.application
import org.springframework.fu.module.webflux.netty.NettyModule
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * @author Sebastien Deleuze
 */
class NettyModuleTests: WebServerModuleTest() {
	override fun getWebServerModule(port: Int, host: String): WebFluxModule.WebServerModule = NettyModule(port)

	@Disabled
	@Test
	override fun `Declare 2 routes blocks`() {
		val context = GenericApplicationContext()
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
		val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()
		client.get().uri("/foo").exchange().expectStatus().isNoContent
		client.get().uri("/bar").exchange().expectStatus().isOk
		context.close()
	}
}