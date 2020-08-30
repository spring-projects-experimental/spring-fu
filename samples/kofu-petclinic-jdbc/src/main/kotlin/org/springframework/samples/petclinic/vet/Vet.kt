package org.springframework.samples.petclinic.vet

import org.springframework.samples.petclinic.model.Person

class Vet : Person() {

    var specialties: Set<Specialty> = hashSetOf()

    fun getSpecialtiesSorted(): List<Specialty> =
            specialties.sortedBy { it.name }.toList()

    fun getNrOfSpecialties() = specialties.size

    fun addSpecialty(specialty: Specialty) {
        specialties += specialty
    }
}