package com.sample

import com.datastax.driver.core.ProtocolOptions
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.core.env.get
import org.springframework.fu.kofu.cassandra.reactiveCassandra
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
	reactiveCassandra {
		keyspaceName = "Kofu"
		port = env["cassandra.port"]?.toInt() ?: ProtocolOptions.DEFAULT_PORT
		// TODO Improve this via a regular mutable property of immutable list
		contactPoints.clear()
		contactPoints.add(env["cassandra.host"] ?: "localhost")
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
