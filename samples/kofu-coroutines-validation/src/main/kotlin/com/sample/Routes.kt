package com.sample

import org.springframework.web.reactive.function.server.coRouter

fun routes(userHandler: UserHandler) = coRouter {
    POST("/api/user", userHandler::createApi)
}
