package com.example

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.server

val app = application {
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		router {
			GET("/") { ok().syncBody("Hello world!") }
		}
	}
}

fun main(args: Array<String>) = app.run()
