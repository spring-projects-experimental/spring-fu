package com.sample

import org.springframework.fu.kofu.web.server
import org.springframework.fu.kofu.webApplication
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok

val app = webApplication {
	beans {
		bean<SampleService>()
		bean<SampleHandler>()
	}
	server {
		port = if (profiles.contains("test")) 8181 else 8080
		router {
			val handler = ref<SampleHandler>()
			GET("/", handler::hello)
			GET("/api", handler::json)
		}
		codecs {
			string()
			jackson()
		}
	}
}

data class Sample(val message: String)

class SampleService {
	fun generateMessage() = "Hello world!"
}

class SampleHandler(private val sampleService: SampleService) {
	fun hello(request: ServerRequest)= ok().syncBody(sampleService.generateMessage())
	fun json(request: ServerRequest) = ok().syncBody(Sample(sampleService.generateMessage()))
}

fun main() {
	app.run()
}
