package org.springframework.fu.sample

import org.springframework.fu.module.webflux.routes

val routes = routes {
	val userHandler = ref<UserHandler>()
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/conf", userHandler::conf)
}