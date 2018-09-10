package com.example

import org.springframework.boot.application
import org.springframework.boot.autoconfigure.web.reactive.netty
import org.springframework.boot.autoconfigure.web.reactive.server


val app = application {
	val port = if (profiles.contains("test")) 8181 else 8080
	server(netty(port)) {
		router {
			GET("/") { ok().syncBody("Hello world!") }
		}
	}
}

fun main(args: Array<String>) = app.run()