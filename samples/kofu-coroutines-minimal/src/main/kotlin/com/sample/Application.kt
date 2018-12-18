package com.sample

import org.springframework.fu.kofu.web.server
import org.springframework.fu.kofu.webApplication

val app = webApplication {
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		coRouter {
			GET("/") { ok().syncBody("Hello world!") }
		}
	}
}

fun main() {
	app.run()
}
