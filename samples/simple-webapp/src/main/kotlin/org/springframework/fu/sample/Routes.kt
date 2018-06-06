package org.springframework.fu.sample

import org.springframework.fu.ref

val reactorRoutes = org.springframework.fu.module.webflux.routes {

	val userHandler = ref<ReactorUserHandler>()

	GET("/reactor", userHandler::listView)
	GET("/reactor/api/user", userHandler::listApi)
	GET("/reactor/conf", userHandler::conf)
}

// TODO Raise a bug about method references noy working with Coroutines
val coroutinesRoutes = org.springframework.fu.module.webflux.coroutines.routes {

	val userHandler = ref<CoroutineUserHandler>()

	GET("/coroutine") { userHandler.listView(it) }
	GET("/coroutine/api/user") { userHandler.listApi(it) }
	GET("/coroutine/conf") { userHandler.conf(it) }
}
