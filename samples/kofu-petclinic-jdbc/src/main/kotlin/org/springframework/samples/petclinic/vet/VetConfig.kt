package org.springframework.samples.petclinic.vet

import org.springframework.fu.kofu.configuration
import org.springframework.web.servlet.function.router

val vetConfig = configuration {
    // Lambda based syntax for native application compat because of https://github.com/oracle/graal/issues/2500
    beans {
        bean { JdbcVetRepositoryImpl(ref()) }
        bean(::vetRoutes)
    }
}

fun vetRoutes(vetRepository: VetRepository) = router {
    GET("/vets.html") {
        ok().render("vets/vetList", mapOf("vets" to vetRepository.findAll().toList()))
    }
    GET("/vets") {
        ok().body(object { val vets = vetRepository.findAll().toList()})
    }
}