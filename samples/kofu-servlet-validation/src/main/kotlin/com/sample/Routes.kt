package com.sample

import org.springframework.web.servlet.function.router

fun routes(userHandler: UserHandler) = router {
    POST("/api/user", userHandler::createApi)
}