package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webflux.webClient
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.fu.kofu.reactiveWebApplication

fun jacksonDsl() {
	reactiveWebApplication {
		webFlux {
			codecs {
				jackson()
			}
		}
		webClient {
			codecs {
				jackson()
			}
		}
	}
}