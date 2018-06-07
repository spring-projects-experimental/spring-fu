package org.springframework.fu.sample.coroutines

import org.springframework.fu.module.webflux.coroutines.web.function.server.CoroutineServerRequest
import org.springframework.fu.module.webflux.coroutines.web.function.server.CoroutineServerResponse
import org.springframework.fu.module.webflux.coroutines.web.function.server.CoroutineServerResponse.Companion.ok
import org.springframework.fu.module.webflux.coroutines.web.function.server.body

@Suppress("UNUSED_PARAMETER")
class UserHandler(private val repository: UserRepository,
				  private val configuration: SampleConfiguration) {

	suspend fun listApi(request: CoroutineServerRequest) = ok()
			.body(repository.findAll())

	suspend fun listView(request: CoroutineServerRequest) =
			ok().render("users", mapOf("users" to repository.findAll()))

	suspend fun conf(request: CoroutineServerRequest) =
			ok().syncBody(configuration.property)
}