package com.example

import org.springframework.boot.application
import org.springframework.boot.autoconfigure.web.reactive.netty
import org.springframework.boot.autoconfigure.web.reactive.server
import org.springframework.boot.autoconfigure.web.reactive.coroutines.coRouter

val app = application {
	val port = if (profiles.contains("test")) 8181 else 8080
	server(netty(port)) {
		coRouter {
			GET("/") { ok().syncBody("Hello world!") }
		}
	}
}

fun main(args: Array<String>) = app.run()