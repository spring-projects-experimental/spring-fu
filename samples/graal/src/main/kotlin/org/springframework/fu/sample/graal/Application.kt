package org.springframework.fu.sample.graal

import org.springframework.boot.application
import org.springframework.boot.autoconfigure.web.reactive.netty
import org.springframework.boot.autoconfigure.web.reactive.server
import org.springframework.core.io.ClassPathResource


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
