package com.sample

import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.r2dbc.r2dbc
import org.springframework.fu.kofu.web.jackson
import org.springframework.fu.kofu.web.mustache
import org.springframework.fu.kofu.web.server

val dataConfig = configuration {
	beans {
		bean<UserRepository>()
	}
	listener<ApplicationReadyEvent> {
		runBlocking {
			ref<UserRepository>().init()
		}
	}
	r2dbc {
		coroutines = true
	}
}

val webConfig = configuration {
	beans {
		bean<UserHandler>()
	}
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		mustache()
		codecs {
			string()
			jackson()
		}
		import(::routes)
	}
}
