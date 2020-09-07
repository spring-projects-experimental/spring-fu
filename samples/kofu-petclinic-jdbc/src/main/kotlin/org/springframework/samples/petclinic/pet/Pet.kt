package org.springframework.samples.petclinic.pet

import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.visit.Visit
import java.time.LocalDate

data class Pet(
        val id: Int? = null,
        val name: String,
        val birthDate: LocalDate,
        val type: PetType,
        val owner: Owner,
        val visits: Set<Visit> = linkedSetOf()
) {
    fun isNew() = id == null
}
