package com.sample

import kotlinx.coroutines.FlowPreview
import org.springframework.web.reactive.function.server.coRouter

@FlowPreview
fun routes(userHandler: UserHandler) = coRouter {
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/conf", userHandler::conf)
}
