package com.sample

import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

/**
 * @author Kevin Davin
 */
@Suppress("UNUSED_PARAMETER")
class UserHandler(
		private val repository: UserRepository,
		private val properties: SampleProperties
) {

	fun listApi(request: ServerRequest) = ServerResponse
			.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(repository.findAll())

	fun userApi(request: ServerRequest): ServerResponse {
		val user = repository.findOne(request.pathVariable("login"))

		return if (user != null) {
			ServerResponse
					.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(user)
		} else {
			ServerResponse.notFound().build()
		}
	}

	fun conf(request: ServerRequest) = ServerResponse
			.ok()
			.body(properties.message)

}
