package org.springframework.fu.sample.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.web.reactive.function.server.coRouter

@ExperimentalCoroutinesApi
fun routes(userHandler: UserHandler) = coRouter {
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/conf", userHandler::conf)
}
