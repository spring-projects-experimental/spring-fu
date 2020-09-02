package org.springframework.samples.petclinic.owner

import org.springframework.dao.DataAccessException

interface OwnerRepository {

    @Throws(DataAccessException::class)
    fun findByLastName(lastName: String): Collection<Owner>

    @Throws(DataAccessException::class)
    fun findById(ownerId: Int): Owner

    @Throws(DataAccessException::class)
    fun save(owner: Owner): Owner
}
