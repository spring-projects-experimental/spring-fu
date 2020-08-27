package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.security
import org.springframework.fu.kofu.webmvc.webMvc

fun webMvcSecurity() {
	webApplication {
		webMvc {
			security {
				// authenticationManager = repoAuthenticationManager
				http = {
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
