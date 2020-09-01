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

package org.springframework.fu.kofu.webmvc

import org.junit.jupiter.api.Test
import org.springframework.fu.kofu.localServerPort
import org.springframework.fu.kofu.webApplication
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.charset.Charset
import java.time.Duration
import java.util.*


/**
 * @author Fred Montariol
 */
class SecurityDslTests {

	@Test
	fun `Check spring-security configuration DSL with provided userDetailsService`() {

		val username = "user"
		val password = "password"

		val app = webApplication {
			webMvc {
				port = 0
				router {
					GET("/public-view") { ok().build() }
					GET("/view") { ok().build() }
				}

				security {
					userDetailsService = userDetailsService(username, password)

					http {
						authorizeRequests {
							authorize("/public-view", permitAll)
							authorize("/view", hasRole("USER"))
						}
						httpBasic {}
					}
				}
			}
		}
		app.run().use { context ->
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:${context.localServerPort}")
					.responseTimeout(Duration.ofMinutes(10)) // useful for debug
					.build()

			client.get().uri("/public-view").exchange()
					.expectStatus().is2xxSuccessful

			client.get().uri("/view").exchange()
					.expectStatus().isUnauthorized

			val basicAuth =
					Base64.getEncoder().encode("$username:$password".toByteArray()).toString(Charset.defaultCharset())
			client.get().uri("/view").header("Authorization", "Basic $basicAuth").exchange()
					.expectStatus().is2xxSuccessful

			val basicAuthWrong =
					Base64.getEncoder().encode("user:pass".toByteArray()).toString(Charset.defaultCharset())
			client.get().uri("/view").header("Authorization", "Basic $basicAuthWrong").exchange()
					.expectStatus().isUnauthorized
		}
	}

	@Test
	fun `Check spring-security configuration DSL with provided authenticationManager`() {

		val username = "user"
		val password = "password"
		val authProvider = DaoAuthenticationProvider()
		authProvider.setUserDetailsService(userDetailsService(username, password))
		val repoAuthenticationManager = ProviderManager(authProvider)

		val app = webApplication {
			webMvc {
				port = 0
				router {
					GET("/public-view") { ok().build() }
					GET("/view") { ok().build() }
				}

				security {
					authenticationManager = repoAuthenticationManager

					http {
						authorizeRequests {
							authorize("/public-view", permitAll)
							authorize("/view", hasRole("USER"))
						}
						httpBasic {}
					}
				}
			}
		}
		app.run().use { context ->
			val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:${context.localServerPort}")
					.responseTimeout(Duration.ofMinutes(10)) // useful for debug
					.build()

			client.get().uri("/public-view").exchange()
					.expectStatus().is2xxSuccessful

			client.get().uri("/view").exchange()
					.expectStatus().isUnauthorized

			val basicAuth =
					Base64.getEncoder().encode("$username:$password".toByteArray()).toString(Charset.defaultCharset())
			client.get().uri("/view").header("Authorization", "Basic $basicAuth").exchange()
					.expectStatus().is2xxSuccessful

			val basicAuthWrong =
					Base64.getEncoder().encode("user:pass".toByteArray()).toString(Charset.defaultCharset())
			client.get().uri("/view").header("Authorization", "Basic $basicAuthWrong").exchange()
					.expectStatus().isUnauthorized
		}
	}

	private fun userDetailsService(username: String, password: String) =
			InMemoryUserDetailsManager(
					@Suppress("DEPRECATION")
					User.withDefaultPasswordEncoder()
							.username(username)
							.password(password)
							.roles("USER")
							.build()
			)
}
