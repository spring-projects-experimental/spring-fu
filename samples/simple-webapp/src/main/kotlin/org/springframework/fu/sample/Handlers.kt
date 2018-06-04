package org.springframework.fu.sample

import org.springframework.fu.module.webflux.ok
import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.body

class UserHandler(private val repository: UserRepository, private val configuration: SampleConfiguration) {

	fun listApi(request: ServerRequest) = ok()
			.header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
			.body(repository.findAll())


	fun listView(request: ServerRequest) =
			ok().render("users", mapOf("users" to repository.findAll()))

	fun conf(request: ServerRequest) =
			ok().syncBody(configuration.property)
}