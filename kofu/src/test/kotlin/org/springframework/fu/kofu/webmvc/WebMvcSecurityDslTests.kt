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
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.fu.kofu.*
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.csrf.DefaultCsrfToken
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * @author Fred Montariol
 */
class WebMvcSecurityDslTests {

	@Test
	fun `Check spring-security configuration DSL with provided userDetailsService`() {
		val app = webApplication {
			webMvc {
				port = 0

				router {
					GET("/public-view") { ok().build() }
					GET("/view") { ok().build() }
					POST("/public-post") { ok().build() }
				}

				security {
					userDetailsService = userDetailsService()

					http {
						authorizeRequests {
							authorize("/public-view", permitAll)
							authorize("/view", hasRole("USER"))
							authorize("/public-post", permitAll)
						}
						httpBasic {}
						csrf {
							csrfTokenRepository = csrfTokenRepository()
						}
					}
				}
			}
		}
		app.run().apply {
			// verify that beans are present in context
			getBean<HttpSecurity>()

			testSecurityWebMvc(this)

			close()
		}
	}

	@Test
	fun `Check spring-security configuration DSL with provided authenticationManager`() {
		val authProvider = DaoAuthenticationProvider()
		authProvider.setUserDetailsService(userDetailsService())
		val repoAuthenticationManager = ProviderManager(authProvider)

		val app = webApplication {
			webMvc {
				port = 0

				router {
					GET("/public-view") { ok().build() }
					GET("/view") { ok().build() }
					POST("/public-post") { ok().build() }
				}

				security {
					authenticationManager = repoAuthenticationManager

					http {
						authorizeRequests {
							authorize("/public-view", permitAll)
							authorize("/view", hasRole("USER"))
							authorize("/public-post", permitAll)
						}
						httpBasic {}
						csrf {
							csrfTokenRepository = csrfTokenRepository()
						}
					}
				}
			}
		}
		app.run().apply {
			// verify that beans are present in context
			getBean<HttpSecurity>()

			testSecurityWebMvc(this)

			close()
		}
	}

	private fun userDetailsService() =
			InMemoryUserDetailsManager(
					@Suppress("DEPRECATION")
					User.withDefaultPasswordEncoder()
							.username(usernameTest)
							.password(passwordTest)
							.roles("USER")
							.build()
			)

	private fun csrfTokenRepository() = object : CsrfTokenRepository {

		override fun generateToken(request: HttpServletRequest) =
				DefaultCsrfToken(csrfHeaderTest, "not_used", csrfTokenTest)

		override fun saveToken(token: CsrfToken?, request: HttpServletRequest, response: HttpServletResponse) {
			if (token == null) {
				request.getSession(false).removeAttribute(csrfSessionAttribute)
			} else {
				request.session.setAttribute(csrfSessionAttribute, token)
			}
		}

		override fun loadToken(request: HttpServletRequest): CsrfToken? {
			val session = request.getSession(false) ?: return null
			return (session.getAttribute(csrfSessionAttribute) as CsrfToken)
		}
	}

	private fun testSecurityWebMvc(context: ApplicationContext) {
		val client = WebTestClient.bindToServer()
				.baseUrl("http://127.0.0.1:${context.localServerPort}")
				.responseTimeout(Duration.ofMinutes(10)) // useful for debug
				.build()

		commonTests(client)

		// Verify post succeed with csrf header
		client
				.post().uri("/public-post")
				.header(csrfHeaderTest, csrfTokenTest)
				.exchange()
				.expectStatus().is2xxSuccessful
	}
}
