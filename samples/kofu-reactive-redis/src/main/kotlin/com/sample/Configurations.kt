package com.sample

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.redis.reactiveRedis
import org.springframework.fu.kofu.templating.mustache
import org.springframework.fu.kofu.webflux.webFlux

fun dataConfig(redisHost: String, redisPort: Int) = configuration {
	beans {
		bean<UserRepository>()
	}
	listener<ApplicationReadyEvent> {
		ref<UserRepository>().init()
	}
	reactiveRedis {
		host = redisHost
		port = redisPort
	}
}

fun webConfig(serverPort: Int) = configuration {
	beans {
		bean<UserHandler>()
		bean(::routes)
	}
	webFlux {
		port = serverPort
		mustache()
		codecs {
			string()
			jackson()
		}
	}
}