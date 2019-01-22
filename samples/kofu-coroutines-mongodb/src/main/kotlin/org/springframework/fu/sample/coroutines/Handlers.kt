package org.springframework.fu.sample.coroutines

import org.springframework.http.MediaType
import org.springframework.web.function.server.*
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Suppress("UNUSED_PARAMETER")
class UserHandler(
		private val repository: UserRepository,
		private val configuration: SampleProperties
) {

	suspend fun listApi(request: ServerRequest) =
		ok().contentType(MediaType.APPLICATION_JSON_UTF8).bodyAndAwait(repository.findAll())


	suspend fun listView(request: ServerRequest) =
		ok().renderAndAwait("users", mapOf("users" to repository.findAll()))


	suspend fun conf(request: ServerRequest) =
		ok().bodyAndAwait(configuration.message)

}