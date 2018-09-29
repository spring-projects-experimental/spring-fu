package com.example

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.server
import org.springframework.web.function.server.coRouter

val router = coRouter {
	GET("/") { ok().syncBody("Hello world!") }
}

val app = application {
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		import(router)
	}
}

fun main(args: Array<String>) = app.run(args)
