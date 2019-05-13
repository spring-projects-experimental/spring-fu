package com.sample

import org.springframework.web.reactive.function.server.router

fun routes(userHandler: UserHandler) = router {
    POST("/api/user", userHandler::createApi)
}
