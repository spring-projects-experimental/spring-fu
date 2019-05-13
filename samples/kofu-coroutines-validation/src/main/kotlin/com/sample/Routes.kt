package com.sample

import kotlinx.coroutines.FlowPreview
import org.springframework.web.reactive.function.server.coRouter

@FlowPreview
fun routes(userHandler: UserHandler) = coRouter {
    POST("/api/user", userHandler::createApi)
}
