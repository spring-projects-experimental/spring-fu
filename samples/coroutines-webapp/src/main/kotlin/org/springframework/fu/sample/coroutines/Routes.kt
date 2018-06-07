package org.springframework.fu.sample.coroutines

import org.springframework.fu.module.webflux.coroutines.routes
import org.springframework.fu.ref

// TODO Raise a bug about method references noy working with Coroutines
val routes = routes {
	val userHandler = ref<UserHandler>()
	GET("/") { userHandler.listView(it) }
	GET("/api/user") { userHandler.listApi(it) }
	GET("/conf") { userHandler.conf(it) }
}
