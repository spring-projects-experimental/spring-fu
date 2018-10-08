package org.springframework.fu.sample.coroutines

import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.embedded
import org.springframework.fu.kofu.mongo.mongodb
import org.springframework.fu.kofu.ref
import org.springframework.fu.kofu.web.jackson
import org.springframework.fu.kofu.web.mustache
import org.springframework.fu.kofu.web.server
import org.springframework.context.support.beans

val beans = beans {
	bean<UserRepository>()
	bean<UserHandler>()
}
val app = application {
	import(beans)
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
		import(::routes)
	}
	mongodb {
		coroutines = true
		embedded {
			version = "3.2.2"
		}
	}
}

fun main(args: Array<String>) = app.run(args)
