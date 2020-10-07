package org.springframework.samples.petclinic.pet

import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.router

val petConfig = configuration {
    // Lambda based syntax for native application compat because of https://github.com/oracle/graal/issues/2500
    beans {
        bean { JdbcPetRepositoryImpl(ref(), ref(), ref()) }
        bean { PetHandler(ref(), ref()) }
        bean(::petRoutes)
        bean { PetTypeFormatter(ref()) }
    }
}

fun petRoutes(petHandler: PetHandler) = router {

    "/owners/{ownerId}/pets".nest {

        GET("/new", petHandler::getPetCreationView)

        POST("/new", petHandler::addPet)

        GET("/{petId}/edit", petHandler::getUpdatePetView)

        POST("/{petId}/edit", petHandler::updatePet)
    }
}