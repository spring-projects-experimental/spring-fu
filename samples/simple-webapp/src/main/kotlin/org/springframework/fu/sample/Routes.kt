package org.springframework.fu.sample

import org.springframework.fu.module.webflux.routes
import org.springframework.fu.ref

val routes = routes {
	val userHandler = ref<UserHandler>()
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/conf", userHandler::conf)
}