package org.springframework.samples.petclinic.pet

import org.springframework.dao.DataAccessException

interface PetRepository{

    @Throws(DataAccessException::class)
    fun findPetTypes(): List<PetType>

    @Throws(DataAccessException::class)
    fun findById(petId: Int): Pet

    @Throws(DataAccessException::class)
    fun findByOwnerId(ownerId: Int): Set<Pet>

    @Throws(DataAccessException::class)
    fun save(pet: Pet)
}