package org.springframework.fu.sample.coroutines

import kotlinx.coroutines.experimental.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongodb.coroutine
import org.springframework.fu.kofu.mongodb.embedded
import org.springframework.fu.kofu.mongodb.mongodb
import org.springframework.fu.kofu.webflux.netty
import org.springframework.fu.kofu.webflux.server
import org.springframework.fu.kofu.ref
import org.springframework.fu.kofu.webflux.jackson
import org.springframework.fu.kofu.webflux.mustache


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
		coroutine()
		embedded()
	}
}

fun main(args: Array<String>) = app.run(args)
