package com.sample

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.core.env.get
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.webflux.mustache
import org.springframework.fu.kofu.webflux.webFlux

val dataConfig = configuration {
	beans {
		bean<UserRepository>()
	}
	listener<ApplicationReadyEvent> {
		ref<UserRepository>().init()
	}
	reactiveRedis {
		host = env["redis.host"] ?: "localhost"
		port = env["redis.port"] ?: 6379
		lettuce()
	}
}

val webConfig = configuration {
	beans {
		bean<UserHandler>()
		bean(::routes)
	}
	webFlux {
		port = if (profiles.contains("test")) 8181 else 8080
		mustache()
		codecs {
			string()
			jackson()
		}
	}
}