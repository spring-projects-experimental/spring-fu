package org.springframework.fu.sample.coroutines

import org.springframework.http.MediaType
import org.springframework.web.function.server.CoroutinesServerRequest
import org.springframework.web.function.server.body
import org.springframework.web.function.server.coHandler

@Suppress("UNUSED_PARAMETER")
class UserHandler(
		private val repository: UserRepository,
		private val configuration: SampleConfiguration
) {

	suspend fun listApi(request: CoroutinesServerRequest) = coHandler {
		ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(repository.findAll())
	}

	suspend fun listView(request: CoroutinesServerRequest) = coHandler {
		ok().render("users", mapOf("users" to repository.findAll()))
	}

	suspend fun conf(request: CoroutinesServerRequest) = coHandler {
		ok().syncBody(configuration.message)
	}

}