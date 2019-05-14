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

package org.springframework.fu.kofu.webflux

import org.junit.jupiter.api.Test
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.localServerPort
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.charset.Charset
import java.util.*


/**
 * @author Jonas Bark
 */
class SecurityDslTests {

    @Test
    fun `Check spring-security configuration DSL`() {

        val username = "user"
        val password = "password"
        val repoAuthenticationManager =
            UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService(username, password))

        val app = application(WebApplicationType.REACTIVE) {
            security {
                authenticationManager = repoAuthenticationManager
                http {
                    authenticationManager(repoAuthenticationManager)
                    headers()
                    logout()
                }
            }
            webFlux {
                port = 0
                webFluxSecurity()
                router {
                    GET("/view") { ok().build() }
                }
            }
        }
        with(app.run()) {
            val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
            client.get().uri("/view").exchange()
                .expectStatus().isUnauthorized

            val basicAuth =
                Base64.getEncoder().encode("$username:$password".toByteArray())?.toString(Charset.defaultCharset())
            client.get().uri("/view").header("Authorization", "Basic $basicAuth").exchange()
                .expectStatus().is2xxSuccessful

            close()
        }
    }

    private fun userDetailsService(username: String, password: String): MapReactiveUserDetailsService {
        @Suppress("DEPRECATION")
        val user = User.withDefaultPasswordEncoder()
            .username(username)
            .password(password)
            .roles("USER")
            .build()
        return MapReactiveUserDetailsService(user)
    }

}
