package com.example

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.netty
import org.springframework.boot.kofu.web.server
import org.springframework.boot.kofu.web.coRouter

val app = application {
	val port = if (profiles.contains("test")) 8181 else 8080
	server(netty(port)) {
		coRouter {
			GET("/") { ok().syncBody("Hello world!") }
		}
	}
}

fun main(args: Array<String>) = app.run()