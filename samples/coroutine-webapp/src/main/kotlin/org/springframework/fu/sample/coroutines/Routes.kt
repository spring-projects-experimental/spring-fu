package org.springframework.fu.sample.coroutines

import org.springframework.fu.module.webflux.coroutine.coRouter

// TODO Use function references when https://youtrack.jetbrains.com/issue/KT-16908 will be fixed
fun routes(userHandler: UserHandler) = coRouter {
	GET("/") { userHandler.listView() }
	GET("/api/user") { userHandler.listApi() }
	GET("/conf") { userHandler.conf() }
}

