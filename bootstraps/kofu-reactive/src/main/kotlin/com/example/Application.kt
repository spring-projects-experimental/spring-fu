package com.example

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.server
import org.springframework.web.reactive.function.server.router

val router = router {
	GET("/") { ok().syncBody("Hello world!") }
}

val app = application {
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		import(router)
	}
}

fun main(args: Array<String>) = app.run(args)
