package com.sample

import org.springframework.web.reactive.function.server.router

fun routes(userHandler: UserHandler) = router {
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/api/user/{login}", userHandler::userApi)
	GET("/conf", userHandler::conf)
}
