package com.sample

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.jackson
import org.springframework.fu.kofu.web.server

val app = application {
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		router {
			GET("/") { ok().syncBody("Hello world!") }
			GET("/api") {
				ok().syncBody(Foo("Hello world!"))
			}
		}
		codecs {
			string()
			jackson()
		}
	}
}

data class Foo(val message: String)

fun main() {
	if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
		System.setProperty("org.springframework.boot.logging.LoggingSystem", "org.springframework.boot.logging.java.JavaLoggingSystem")
	}
	app.run()
}
