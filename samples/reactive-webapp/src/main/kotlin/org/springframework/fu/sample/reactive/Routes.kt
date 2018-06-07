package org.springframework.fu.sample.reactive

import org.springframework.fu.module.webflux.routes
import org.springframework.fu.ref

fun reactiveRoutes() = routes {
	val userHandler = ref<UserHandler>()
	GET("/", userHandler::listView)
	GET("/api/user", userHandler::listApi)
	GET("/conf", userHandler::conf)
}
