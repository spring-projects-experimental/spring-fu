package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.mustache
import org.springframework.fu.kofu.webflux.webFlux

fun mustacheDsl() {
	application(WebApplicationType.REACTIVE) {
		webFlux {
			mustache()
		}
	}
}