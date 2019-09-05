package com.sample

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.web.reactive.function.server.coRouter

@ExperimentalCoroutinesApi
fun routes(userHandler: UserHandler) = coRouter {
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/api/user/{login}", userHandler::userApi)
	GET("/conf", userHandler::conf)
}
