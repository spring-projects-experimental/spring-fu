package org.springframework.fu.sample.reactive

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@Suppress("UNUSED_PARAMETER")
class UserHandler(private val repository: UserRepository,
				  private val configuration: SampleConfiguration) {

	fun listApi(request:ServerRequest) = ok()
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(repository.findAll())

	fun listView(request: ServerRequest) =
			ok().render("users", mapOf("users" to repository.findAll()))

	fun conf(request: ServerRequest) =
			ok().syncBody(configuration.property)
}