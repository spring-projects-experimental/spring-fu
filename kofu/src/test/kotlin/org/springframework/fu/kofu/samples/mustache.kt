package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.fu.kofu.webflux.mustache
import org.springframework.fu.kofu.webflux.webFlux

fun mustacheDsl() {
	reactiveWebApplication {
		webFlux {
			mustache()
		}
	}
}