/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.fu.kofu.webflux

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.fu.kofu.*
import org.springframework.fu.kofu.javautils.JavaKotlinUtils
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration


/**
 * @author Jonas Bark, Ivan Skachkov, Fred Montariol
 */
class WebFluxSecurityDslTests {

	@Test
	fun `Check spring-security configuration DSL with provided userDetailsService`() {
		val app = reactiveWebApplication {
			webFlux {
				port = 0

				router {
					GET("/public-view") { ok().build() }
					GET("/view") { ok().build() }
					POST("/public-post") { ok().build() }
				}

				security {
					userDetailsService = userDetailsService()

					http {
						authorizeExchange {
							authorize("/public-view", permitAll)
							authorize("/view", hasRole("USER"))
							authorize("/public-post", permitAll)
						}
						httpBasic {}
					}
				}
			}
		}
		app.run().apply {
			// verify that beans are present in context
			getBean<ServerHttpSecurity>()

			testSecurityWebFlux(this)

			close()
		}
	}

	@Test
	fun `Check spring-security configuration DSL with provided authenticationManager`() {
		val repoAuthenticationManager =
				UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService())

		val app = reactiveWebApplication {
			webFlux {
				port = 0

				router {
					GET("/public-view") { ok().build() }
					GET("/view") { ok().build() }
					POST("/public-post") { ok().build() }
				}

				security {
					authenticationManager = repoAuthenticationManager

					http {
						authorizeExchange {
							authorize("/public-view", permitAll)
							authorize("/view", hasRole("USER"))
							authorize("/public-post", permitAll)
						}
						httpBasic {}
					}
				}
			}
		}
		app.run().apply {
			// verify that beans are present in context
			getBean<ServerHttpSecurity>()

			testSecurityWebFlux(this)

			close()
		}
	}

	private fun userDetailsService() =
			MapReactiveUserDetailsService(
					@Suppress("DEPRECATION")
					User.withDefaultPasswordEncoder()
							.username(usernameTest)
							.password(passwordTest)
							.roles("USER")
							.build()
			)

	private fun testSecurityWebFlux(context: ApplicationContext) {
		val client = WebTestClient.bindToServer()
				.baseUrl("http://127.0.0.1:${context.localServerPort}")
				.responseTimeout(Duration.ofMinutes(10)) // useful for debug
				.build()

		commonTests(client)

		val mockClient = JavaKotlinUtils.getMockWebTestClient(context)

		// Verify post fails without csrf header (mock client)
		mockClient
				.post().uri("/public-post")
				.exchange()
				.expectStatus().isForbidden

		// Verify post succeed with csrf header
		mockClient
				.mutateWith(SecurityMockServerConfigurers.csrf())
				.post().uri("/public-post")
				.exchange()
				.expectStatus().is2xxSuccessful
	}
}
