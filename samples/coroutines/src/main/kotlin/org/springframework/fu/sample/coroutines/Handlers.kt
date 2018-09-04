package org.springframework.fu.sample.coroutines

import org.springframework.fu.coroutines.webflux.web.function.server.CoroutinesServerRequest
import org.springframework.fu.coroutines.webflux.web.function.server.body
import org.springframework.fu.coroutines.webflux.web.function.server.coroutineHandler
import org.springframework.http.MediaType

@Suppress("UNUSED_PARAMETER")
class UserHandler(
	private val repository: UserRepository,
	private val configuration: SampleConfiguration
) {

	suspend fun listApi(request: CoroutinesServerRequest) = coroutineHandler {
		ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(repository.findAll())
	}

	suspend fun listView(request: CoroutinesServerRequest) = coroutineHandler {
		ok().render("users", mapOf("users" to repository.findAll()))
	}

	suspend fun conf(request: CoroutinesServerRequest) = coroutineHandler {
		ok().syncBody(configuration.message)
	}

}