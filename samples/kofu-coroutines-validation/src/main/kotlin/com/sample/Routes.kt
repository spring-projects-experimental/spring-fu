package com.sample

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.web.reactive.function.server.coRouter

@ExperimentalCoroutinesApi
fun routes(userHandler: UserHandler) = coRouter {
    POST("/api/user", userHandler::createApi)
}
