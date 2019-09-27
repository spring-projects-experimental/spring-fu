package com.sample

import org.springframework.web.reactive.function.server.coRouter

fun routes(userHandler: UserHandler) = coRouter {
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/api/user/{login}", userHandler::userApi)
	GET("/conf", userHandler::conf)
}
