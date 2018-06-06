package org.springframework.fu.sample

import org.springframework.fu.module.webflux.coroutines.web.function.server.CoroutineServerRequest
import org.springframework.fu.module.webflux.coroutines.web.function.server.CoroutineServerResponse
import org.springframework.fu.module.webflux.coroutines.web.function.server.body
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body


@Suppress("UNUSED_PARAMETER")
class ReactorUserHandler(private val repository: ReactorUserRepository,
						 private val configuration: SampleConfiguration) {

	fun listApi(request: ServerRequest) =
			ServerResponse.ok()
					.body(repository.findAll())

	fun listView(request: ServerRequest) =
			ServerResponse.ok()
					.render("users", mapOf("users" to repository.findAll()))

	fun conf(request: ServerRequest) =
			ServerResponse.ok()
					.syncBody(configuration.property)
}

@Suppress("UNUSED_PARAMETER")
class CoroutineUserHandler(private val repository: CoroutineUserRepository,
						   private val configuration: SampleConfiguration) {

	suspend fun listApi(request: CoroutineServerRequest) =
			CoroutineServerResponse.ok()
					.body(repository.findAll())

	suspend fun listView(request: CoroutineServerRequest) =
			CoroutineServerResponse.ok()
					.render("users", mapOf("users" to repository.findAll()))

	suspend fun conf(request: CoroutineServerRequest) =
			CoroutineServerResponse.ok()
					.syncBody(configuration.property)
}
