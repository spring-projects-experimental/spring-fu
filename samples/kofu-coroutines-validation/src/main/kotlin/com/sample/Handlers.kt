package com.sample

import am.ik.yavi.fn.awaitFold
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait

@ExperimentalCoroutinesApi
@Suppress("UNUSED_PARAMETER")
class UserHandler {

    suspend fun createApi(request: ServerRequest): ServerResponse =
            request.awaitBody<User>()
                    .validate()
                    .leftMap { mapOf("details" to it.details()) }
					// TODO Fix reference ambiguity when Spring Framework 5.2 GA is available (https://github.com/spring-projects/spring-framework/commit/40a55b412d2caa0ea6e1bc3d641c3bd484c7740f)
					.awaitFold({ badRequest().bodyAndAwait(it) }, { ok().bodyAndAwait(it) })
}