package org.springframework.samples.petclinic.vet

import org.springframework.dao.DataAccessException
import kotlin.jvm.Throws


interface VetRepository {

    @Throws(DataAccessException::class)
    fun findAll(): Collection<Vet>
}