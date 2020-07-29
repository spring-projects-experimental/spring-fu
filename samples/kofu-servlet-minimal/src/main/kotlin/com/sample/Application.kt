package com.sample

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse.ok


val app = webApplication {
	beans {
		bean<SampleService>()
		bean<SampleHandler>()
	}
	webMvc {
		port = if (profiles.contains("test")) 8181 else 8080
		router {
			val handler = ref<SampleHandler>()
			GET("/", handler::hello)
			GET("/api", handler::json)
		}
		converters {
			string()
			jackson()
		}
	}
}


fun main() {
	app.run()
}

data class Sample(val message: String)

class SampleService {
	fun generateMessage() = "Hello world!"
}

@Suppress("UNUSED_PARAMETER")
class SampleHandler(private val sampleService: SampleService) {
	fun hello(request: ServerRequest)= ok().body(sampleService.generateMessage())
	fun json(request: ServerRequest) = ok().body(Sample(sampleService.generateMessage()))
}

