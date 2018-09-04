package org.springframework.fu.sample.coroutines

import org.springframework.fu.web.function.server.CoroutinesServerRequest
import org.springframework.fu.web.function.server.body
import org.springframework.fu.web.function.server.coHandler
import org.springframework.http.MediaType

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