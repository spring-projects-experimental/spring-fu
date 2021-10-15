package com.sample

import am.ik.yavi.fn.awaitFold
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Suppress("UNUSED_PARAMETER")
class UserHandler {

    suspend fun createApi(request: ServerRequest): ServerResponse =
            request.awaitBody<User>()
                    .validate()
                    .mapError { it.detail() }
                    .awaitFold(
                        { badRequest().bodyValueAndAwait(mapOf("details" to it)) },
                        ok()::bodyValueAndAwait
                    )
}