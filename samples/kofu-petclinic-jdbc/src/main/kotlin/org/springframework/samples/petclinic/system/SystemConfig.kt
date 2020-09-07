package org.springframework.samples.petclinic.system

import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.router

val systemConfig = configuration {
    beans {
        bean(::systemRoutes)
    }
}

fun systemRoutes() = router {
    GET("/") {
        ok().render("welcome")
    }
    GET("/oups") {
        throw RuntimeException("Expected: route used to showcase what happens when an exception is thrown")
    }
}
