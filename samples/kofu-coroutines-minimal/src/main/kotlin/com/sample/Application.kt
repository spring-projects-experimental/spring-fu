package com.sample

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.server
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
