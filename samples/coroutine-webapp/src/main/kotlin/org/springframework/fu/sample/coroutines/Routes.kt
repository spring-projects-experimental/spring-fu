package org.springframework.fu.sample.coroutines

import org.springframework.fu.module.webflux.coroutine.coroutineRoutes
import org.springframework.fu.ref

// TODO Use function references when https://youtrack.jetbrains.com/issue/KT-16908 will be fixed
fun routes() = coroutineRoutes {
	val userHandler = ref<UserHandler>()
	GET("/") { userHandler.listView() }
	GET("/api/user") { userHandler.listApi() }
	GET("/conf") { userHandler.conf() }
}

