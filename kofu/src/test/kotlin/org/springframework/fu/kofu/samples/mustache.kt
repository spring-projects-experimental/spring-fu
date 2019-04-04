package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webflux.mustache
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.fu.kofu.reactiveWebApplication

fun mustacheDsl() {
	reactiveWebApplication {
		webFlux {
			mustache()
		}
	}
}