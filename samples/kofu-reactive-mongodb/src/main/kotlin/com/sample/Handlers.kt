package com.sample

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body

@Suppress("UNUSED_PARAMETER")
class UserHandler(
		private val repository: UserRepository,
		private val configuration: SampleProperties
) {

	fun listApi(request: ServerRequest) = ServerResponse
			.ok()
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(repository.findAll())

	fun listView(request: ServerRequest) = ServerResponse
			.ok()
			.render("users", mapOf("users" to repository.findAll()))


	fun conf(request: ServerRequest) = ServerResponse
			.ok()
			.syncBody(configuration.message)

}