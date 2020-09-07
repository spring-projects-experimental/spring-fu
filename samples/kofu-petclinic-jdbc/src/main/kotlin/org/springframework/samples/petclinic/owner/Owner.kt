package org.springframework.samples.petclinic.owner

import org.springframework.samples.petclinic.pet.Pet

data class Owner(
        val id: Int? = null,
        val firstName: String,
        val lastName: String,
        val address: String,
        val city: String,
        val telephone: String,
        val pets: Set<Pet> = hashSetOf()
) {

    fun getPetsSorted(): Set<Pet> =
            pets.sortedBy { it.name }.toHashSet()

    fun addPet(pet: Pet) =
            copy(pets = pets  + pet)

    fun getPet(name: String): Pet? =
            pets.filter { it.name == name.decapitalize() }.firstOrNull()

    fun isNew() = id == null

    companion object
}

