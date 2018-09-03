package com.example

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.netty
import org.springframework.fu.kofu.webflux.server


val app = application {
	val port = if (profiles.contains("test")) 8181 else 8080
	server(netty(port)) {
		router {
			GET("/") { ok().syncBody("Hello world!") }
		}
	}
}

fun main(args: Array<String>) = app.run()