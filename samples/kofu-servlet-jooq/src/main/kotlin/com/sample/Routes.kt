package com.sample

import org.springframework.web.servlet.function.router


fun routes(userHandler: UserHandler) = router {
	GET("/api/user", userHandler::listApi)
	GET("/api/user/{login}", userHandler::userApi)
	GET("/conf", userHandler::conf)
}
