package com.sample

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.jackson
import org.springframework.fu.kofu.web.server
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.router

val router = router {
	GET("/") {
		ok().syncBody("Hello GraalVM native images!")
	}
	GET("/api") {
		ok().syncBody(Foo("Hello GraalVM native images!").toMap())
	}
	resources("/**", ClassPathResource("static/"))
}

data class Foo(val message: String) {

	fun toMap() = mapOf("message" to message)
}

val app = application {
	server {
		import(router)
		codecs {
			string()
			jackson()
		}
	}
}

fun main(args: Array<String>) = app.run(args)