package com.sample

import org.springframework.fu.kofu.web.server
import org.springframework.fu.kofu.webApplication

val app = webApplication {
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
