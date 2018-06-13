package org.springframework.fu.sample.reactive

import org.springframework.fu.module.webflux.routes
import org.springframework.fu.ref

fun routes() = routes {
	val userHandler = ref<UserHandler>()
	GET("/", ref = userHandler::listView)
	GET("/api/user", ref = userHandler::listApi)
	GET("/conf", ref = userHandler::conf)
}
