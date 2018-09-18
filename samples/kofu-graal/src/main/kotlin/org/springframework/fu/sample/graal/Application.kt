package org.springframework.fu.sample.graal

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.server
import org.springframework.core.io.ClassPathResource


val app = application {
	server {
		router {
			GET("/") {
				ok().syncBody("Hello world!")
			}
			resources("/**", ClassPathResource("static/"))
		}
	}
}

fun main(args: Array<String>) = app.run()
