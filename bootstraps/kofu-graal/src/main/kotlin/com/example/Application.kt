package com.example

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.server
import org.springframework.core.io.ClassPathResource


val app = application {
	server {
		router {
			GET("/") {
				ok().syncBody("Hello GraalVM native images!")
			}
			resources("/**", ClassPathResource("static/"))
		}
	}
}

fun main(args: Array<String>) = app.run(args)