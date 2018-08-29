package com.example

import org.springframework.fu.application
import org.springframework.fu.module.webflux.coroutine.coRouter
import org.springframework.fu.module.webflux.webflux

val app = application {
	webflux {
		val port = if (profiles.contains("test")) 8181 else 8080
		server(netty(port)) {
			coRouter {
				GET("/") { ok().syncBody("Hello world!") }
			}
		}
	}
}

fun main(args: Array<String>) = app.run(await = true)