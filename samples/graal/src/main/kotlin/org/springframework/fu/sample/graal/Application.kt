package org.springframework.fu.sample.graal

import org.springframework.core.io.ClassPathResource
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.netty
import org.springframework.fu.kofu.webflux.server

val app = application {
	server(netty()) {
		router {
			GET("/") {
				ok().syncBody("Hello world!")
			}
			resources("/**", ClassPathResource("static/"))
		}
	}
}

fun main(args: Array<String>) = app.run()
