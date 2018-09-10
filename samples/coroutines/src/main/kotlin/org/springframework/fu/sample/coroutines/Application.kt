package org.springframework.fu.sample.coroutines

import kotlinx.coroutines.runBlocking
import org.springframework.boot.application
import org.springframework.boot.autoconfigure.jackson.jackson
import org.springframework.boot.autoconfigure.mongo.coroutines.coroutines
import org.springframework.boot.autoconfigure.mongo.mongodb
import org.springframework.boot.autoconfigure.mustache.mustache
import org.springframework.boot.autoconfigure.web.reactive.netty
import org.springframework.boot.autoconfigure.web.reactive.server
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.ref
import org.springframework.boot.autoconfigure.mongo.embedded.embedded


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
	configuration<SampleConfiguration>("sample")
	val port = if (profiles.contains("test")) 8181 else 8080
	server(netty(port)) {
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
