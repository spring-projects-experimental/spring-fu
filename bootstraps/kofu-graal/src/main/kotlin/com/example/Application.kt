package com.example

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.server
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.router

val router = router {
	GET("/") {
		ok().syncBody("Hello GraalVM native images!")
	}
	resources("/**", ClassPathResource("static/"))
}

val app = application {
	server {
		import(router)
	}
}

fun main(args: Array<String>) = app.run(args)