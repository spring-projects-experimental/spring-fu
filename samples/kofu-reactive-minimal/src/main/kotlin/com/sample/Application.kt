package com.sample

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.server

val app = application {
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		router {
			GET("/") { ok().syncBody("Hello world!") }
		}
	}
}

fun main() = app.run()
