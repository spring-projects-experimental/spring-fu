package org.springframework.fu.module.webflux.server

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

class KotlinHandlerFunction {

	/**
	 * Create a builder with the status code and headers of the given response.
	 * @param other the response to copy the status and headers from
	 * @return the created builder
	 */
	fun from(other: ServerResponse): ServerResponse.BodyBuilder =
			ServerResponse.from(other)

	/**
	 * Create a builder with the given HTTP status.
	 * @param status the response status
	 * @return the created builder
	 */
	fun status(status: HttpStatus): ServerResponse.BodyBuilder =
			ServerResponse.status(status)

	/**
	 * Create a builder with the given HTTP status.
	 * @param status the response status
	 * @return the created builder
	 * @since 5.0.3
	 */
	fun status(status: Int): ServerResponse.BodyBuilder =
			ServerResponse.status(status)

	/**
	 * Create a builder with the status set to [200 OK][HttpStatus.OK].
	 * @return the created builder
	 */
	fun ok(): ServerResponse.BodyBuilder =
			ServerResponse.ok()

	/**
	 * Create a new builder with a [201 Created][HttpStatus.CREATED] status
	 * and a location header set to the given URI.
	 * @param location the location URI
	 * @return the created builder
	 */
	fun created(location: URI): ServerResponse.BodyBuilder =
			ServerResponse.created(location)

	/**
	 * Create a builder with an [202 Accepted][HttpStatus.ACCEPTED] status.
	 * @return the created builder
	 */
	fun accepted(): ServerResponse.BodyBuilder =
			ServerResponse.accepted()

	/**
	 * Create a builder with a [204 No Content][HttpStatus.NO_CONTENT] status.
	 * @return the created builder
	 */
	fun noContent(): ServerResponse.HeadersBuilder<*> =
			ServerResponse.noContent()

	/**
	 * Create a builder with a [303 See Other][HttpStatus.SEE_OTHER]
	 * status and a location header set to the given URI.
	 * @param location the location URI
	 * @return the created builder
	 */
	fun seeOther(location: URI): ServerResponse.BodyBuilder =
			ServerResponse.seeOther(location)

	/**
	 * Create a builder with a [307 Temporary Redirect][HttpStatus.TEMPORARY_REDIRECT]
	 * status and a location header set to the given URI.
	 * @param location the location URI
	 * @return the created builder
	 */
	fun temporaryRedirect(location: URI): ServerResponse.BodyBuilder =
			ServerResponse.temporaryRedirect(location)

	/**
	 * Create a builder with a [308 Permanent Redirect][HttpStatus.PERMANENT_REDIRECT]
	 * status and a location header set to the given URI.
	 * @param location the location URI
	 * @return the created builder
	 */
	fun permanentRedirect(location: URI): ServerResponse.BodyBuilder =
			ServerResponse.permanentRedirect(location)

	/**
	 * Create a builder with a [400 Bad Request][HttpStatus.BAD_REQUEST] status.
	 * @return the created builder
	 */
	fun badRequest(): ServerResponse.BodyBuilder =
			ServerResponse.badRequest()

	/**
	 * Create a builder with a [404 Not Found][HttpStatus.NOT_FOUND] status.
	 * @return the created builder
	 */
	fun notFound(): ServerResponse.HeadersBuilder<*> =
			ServerResponse.notFound()

	/**
	 * Create a builder with an
	 * [422 Unprocessable Entity][HttpStatus.UNPROCESSABLE_ENTITY] status.
	 * @return the created builder
	 */
	fun unprocessableEntity(): ServerResponse.BodyBuilder =
			ServerResponse.unprocessableEntity()
}

fun handler(init: KotlinHandlerFunction.() -> Mono<ServerResponse>) = KotlinHandlerFunction().init()