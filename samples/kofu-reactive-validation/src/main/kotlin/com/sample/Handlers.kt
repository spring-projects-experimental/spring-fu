package com.sample

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono

@Suppress("UNUSED_PARAMETER")
class UserHandler {

    fun createApi(request: ServerRequest) =
            request.bodyToMono<User>()
                    .flatMap { user ->
                        user.validate()
                            .mapError { it.detail() }
                            .fold(
                                { badRequest().bodyValue(mapOf("details" to it)) },
                                ok()::bodyValue
                            )
                    }
}