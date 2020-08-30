package org.springframework.samples.petclinic.owner

import org.springframework.samples.petclinic.model.Person
import org.springframework.samples.petclinic.pet.Pet

class Owner() : Person() {

    var address: String? = null
    var city: String? = null
    var telephone: String? = null
    var pets: Set<Pet> = hashSetOf()

    fun getPetsSorted(): Set<Pet> =
            pets.sortedBy { it.name }.toHashSet()

    fun addPet(pet: Pet) {
        pet.ownerId = this.id
        pet.owner = this
        pets += pet
    }

    fun getPet(name: String): Pet? =
            pets.filter { it.name == name.decapitalize() }.firstOrNull()

    fun isValid() = !firstName.isNullOrBlank()
            && !lastName.isNullOrBlank()
            && !address.isNullOrBlank()
            && !city.isNullOrBlank()
            && !telephone.isNullOrBlank()
}

