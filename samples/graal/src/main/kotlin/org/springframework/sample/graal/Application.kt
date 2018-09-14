package org.springframework.sample.graal

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.netty
import org.springframework.boot.kofu.web.server
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
