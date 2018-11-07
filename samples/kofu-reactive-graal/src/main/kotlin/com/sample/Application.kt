package com.sample

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.jackson
import org.springframework.fu.kofu.web.server
import org.springframework.core.io.ClassPathResource
import org.springframework.fu.kofu.r2dbc.r2dbc
import org.springframework.web.function.server.coRouter

val router = coRouter {
	GET("/") {
		ok().syncBody("Hello GraalVM native images!")
	}
	GET("/api") {
		ok().syncBody(Foo("Hello GraalVM native images!"))
	}
	resources("/**", ClassPathResource("static/"))
}

data class Foo(val message: String)

val app = application {
	server {
		import(router)
		codecs {
			string()
			jackson()
		}
	}
	r2dbc()
}

fun main() = app.run()