package com.example

import org.springframework.fu.application
import org.springframework.fu.module.logging.*
import org.springframework.fu.module.webflux.netty.netty
import org.springframework.fu.module.webflux.webflux
import org.springframework.web.reactive.function.server.ServerResponse

val app = application {
	logging {
		level(LogLevel.INFO)
		logback {
			consoleAppender()
		}
	}
	webflux {
		val port = if (profiles.contains("test")) 8181 else 8080
		server(netty(port)) {
			routes {
				GET("/") { ServerResponse.ok().syncBody("Hello world!") }
			}
		}
	}
}

fun main(args: Array<String>) = app.run(await = true)