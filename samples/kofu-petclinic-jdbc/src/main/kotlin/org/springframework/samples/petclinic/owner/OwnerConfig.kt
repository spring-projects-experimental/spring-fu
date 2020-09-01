package org.springframework.samples.petclinic.owner

import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.router

val ownerConfig = configuration {
    // Lambda based syntax for native application compat because of https://github.com/oracle/graal/issues/2500
    beans {
        bean { JdbcOwnerRepositoryImpl(ref()) }
        bean { OwnerHandler(ref(), ref()) }
        bean(::ownerRoutes)
    }
}

fun ownerRoutes(ownerHandler: OwnerHandler) = router {

    "/owners".nest {

        GET("/new", ownerHandler::getOwnerCreationView)

        POST("/new", ownerHandler::addOwner)

        GET("/find", ownerHandler::findOwnerView)

        GET("/", ownerHandler::findOwnerByLastName)

        GET("/{ownerId}/edit", ownerHandler::getOwnerUpdateView)

        POST("/{ownerId}/edit", ownerHandler::updateOwner)

        GET("/{ownerId}", ownerHandler::getOwnerById)
    }

}