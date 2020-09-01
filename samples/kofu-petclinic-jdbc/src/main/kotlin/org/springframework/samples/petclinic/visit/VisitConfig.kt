package org.springframework.samples.petclinic.visit

import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.router

val visitConfig = configuration {
    // Lambda based syntax for native application compat because of https://github.com/oracle/graal/issues/2500
    beans {
        bean { JdbcVisitRepositoryImpl(ref()) }
        bean { VisitHandler(ref(), ref()) }
        bean(::visitRoutes)
    }
}

fun visitRoutes(visitHandler: VisitHandler) = router {

    "/owners/{ownerId}/pets".nest {

        GET("/{petId}/visits/new", visitHandler::getVisitsView)

        POST("/{petId}/visits/new", visitHandler::addVisit)
    }
}