package com.sample

import org.springframework.http.MediaType
import org.springframework.web.function.server.CoServerRequest
import org.springframework.web.function.server.coHandler
import org.springframework.web.function.server.body

@Suppress("UNUSED_PARAMETER")
class UserHandler(
		private val repository: UserRepository,
		private val configuration: SampleProperties
) {

	suspend fun listApi(request: CoServerRequest) = coHandler {
		ok().contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(repository.findAll())
	}

	suspend fun listView(request: CoServerRequest) = coHandler {
		ok().render("users", mapOf("users" to repository.findAll()))
	}

	suspend fun conf(request: CoServerRequest) = coHandler {
		ok().syncBody(configuration.message)
	}

}