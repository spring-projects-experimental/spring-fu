package com.sample

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyAndAwait

@Suppress("UNUSED_PARAMETER")
class UserHandler(
		private val repository: UserRepository,
		private val properties: SampleProperties
) {

	fun listApi(request: ServerRequest) = ServerResponse
			.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(repository.findAll())

	fun userApi(request: ServerRequest) = ServerResponse
			.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(repository.findOne(request.pathVariable("login")))

	fun listView(request: ServerRequest) = ServerResponse
			.ok()
			.render("users", mapOf("users" to repository.findAll()))


	fun conf(request: ServerRequest) = ServerResponse
			.ok()
			.bodyValue(properties.message)

}