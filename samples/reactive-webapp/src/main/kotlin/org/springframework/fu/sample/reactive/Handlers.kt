package org.springframework.fu.sample.reactive

import org.springframework.fu.module.webflux.server.handler
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.body

@Suppress("UNUSED_PARAMETER")
class UserHandler(private val repository: UserRepository,
				  private val configuration: SampleConfiguration) {

	fun listApi(request: ServerRequest) = handler {
		ok().contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(repository.findAll())
	}

	fun listView(request: ServerRequest) = handler {
		ok().render("users", mapOf("users" to repository.findAll()))
	}

	fun conf(request: ServerRequest) = handler {
		ok().syncBody(configuration.property)
	}
}