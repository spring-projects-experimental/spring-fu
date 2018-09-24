package org.springframework.fu.sample.coroutines

import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.mongo.coroutines
import org.springframework.boot.kofu.mongo.embedded
import org.springframework.boot.kofu.mongo.mongodb
import org.springframework.boot.kofu.ref
import org.springframework.boot.kofu.web.jackson
import org.springframework.boot.kofu.web.mustache
import org.springframework.boot.kofu.web.server

val app = application {
	beans {
		bean<UserRepository>()
		bean<UserHandler>()
	}
	listener<ApplicationReadyEvent> {
		runBlocking {
			ref<UserRepository>().init()
		}
	}
	properties<SampleProperties>("sample")
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		mustache()
		codecs {
			string()
			jackson()
		}
		include { routes(ref()) }
	}
	mongodb {
		coroutines()
		embedded()
	}
}

fun main(args: Array<String>) = app.run(args)
