package org.springframework.fu.sample.coroutines

import org.springframework.fu.module.webflux.coroutines.routes
import org.springframework.fu.ref

// TODO Use function references when https://youtrack.jetbrains.com/issue/KT-16908 will be fixed
fun coroutineRoutes() = routes {
	val userHandler = ref<UserHandler>()
	GET("/") { userHandler.listView(it) }
	GET("/api/user") { userHandler.listApi(it) }
	GET("/conf") { userHandler.conf(it) }
}

