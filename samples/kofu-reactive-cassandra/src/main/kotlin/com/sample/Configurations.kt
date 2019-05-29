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
		port = env["port"]?.toInt() ?: ProtocolOptions.DEFAULT_PORT
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
