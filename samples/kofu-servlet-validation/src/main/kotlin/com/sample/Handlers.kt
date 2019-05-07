package com.sample

import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse.badRequest
import org.springframework.web.servlet.function.ServerResponse.ok

@Suppress("UNUSED_PARAMETER")
class UserHandler {

    fun createApi(request: ServerRequest) =
            request.body(User::class.java)
                    .validate()
                    .leftMap { mapOf("details" to it.details()) }
                    .fold(badRequest()::body, ok()::body)
}