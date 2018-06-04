package org.springframework.fu.module.webflux

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import java.net.URI

fun ok() = ServerResponse.ok()

fun from(other: ServerResponse) = ServerResponse.from(other)

fun status(status: HttpStatus) = ServerResponse.status(status)

fun status(status: Int) = ServerResponse.status(status)

fun created(location: URI) = ServerResponse.created(location)

fun accepted() = ServerResponse.accepted()

fun noContent() = ServerResponse.noContent()

fun seeOther(location: URI) = ServerResponse.seeOther(location)

fun temporaryRedirect(location: URI) = ServerResponse.temporaryRedirect(location)

fun permanentRedirect(location: URI) = ServerResponse.permanentRedirect(location)

fun badRequest() = ServerResponse.badRequest()

fun notFound() = ServerResponse.notFound()

fun unprocessableEntity() = ServerResponse.unprocessableEntity()