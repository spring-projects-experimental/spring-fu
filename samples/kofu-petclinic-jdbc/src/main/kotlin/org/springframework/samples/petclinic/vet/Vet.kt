package org.springframework.samples.petclinic.vet

data class Vet(
        val id: Int,
        val firstName: String,
        val lastName: String,
        val specialties: Set<Specialty> = hashSetOf()
) {
    fun getNrOfSpecialties() = specialties.size
}