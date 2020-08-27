/*
 * Copyright 2012-2020 the original author or authors.
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

package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.fu.kofu.webflux.security
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User

fun webFluxSecurity() {
	reactiveWebApplication {
		webFlux {
			security {
				userDetailsService = userDetailsService()
				http {
					anonymous { }
					authorizeExchange {
						authorize("/view", hasRole("USER"))
						authorize("/public-view", permitAll)
					}
					headers {}
					logout {}
				}
			}
		}
	}
}

private fun userDetailsService() =
		MapReactiveUserDetailsService(
				@Suppress("DEPRECATION")
				User.withDefaultPasswordEncoder()
						.username("username")
						.password("password")
						.roles("USER")
						.build()
		)
