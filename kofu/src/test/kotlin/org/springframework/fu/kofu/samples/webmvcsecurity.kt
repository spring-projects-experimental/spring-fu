package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.security
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager

fun webMvcSecurity() {
	webApplication {
		webMvc {
			security {
				userDetailsService = userDetailsService()
				http {
					anonymous { }
					authorizeRequests {
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
		InMemoryUserDetailsManager(
				@Suppress("DEPRECATION")
				User.withDefaultPasswordEncoder()
						.username("username")
						.password("password")
						.roles("USER")
						.build()
		)
