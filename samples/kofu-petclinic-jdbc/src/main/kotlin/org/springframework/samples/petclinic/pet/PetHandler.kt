package org.springframework.samples.petclinic.pet

import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.petFormParams
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok

class PetHandler(private val pets: PetRepository, private val owners: OwnerRepository) {

    private val createOrUpdatePetView = "pets/createOrUpdatePetForm"

    // those vars are here to avoid multiple request to the db
    private lateinit var petTypes: List<PetType>
    private lateinit var owner: Owner

    fun getPetCreationView(request: ServerRequest): ServerResponse {
        petTypes = pets.findPetTypes()
        owner = owners.findById(request.pathVariable("ownerId").toInt())
        return ok().render(createOrUpdatePetView, mapOf("pet" to Pet(), "types" to petTypes.map { it.name }, "owner" to owner))
    }

    fun addPet(request: ServerRequest): ServerResponse {
        return try {
            val ownerId = request.pathVariable("ownerId").toInt()
            // val pet = request.form<Pet>() // TODO Fix this
            val pet = request.petFormParams(petTypes)

            if (owner.pets.any { it.name == pet.name }) { // TODO replace with comparison on owner.getPet() once nullability has been removed
                // TODO : Manage errors for thymeleaf (specific format ?) FieldError("name", "duplicate", "already exists")
                throw IllegalAccessException("duplicate")
            }

            owner.addPet(pet)
            pets.save(pet)
            ok().render("redirect:/owners/${ownerId}")

        } catch (e: IllegalArgumentException) { // Reflect error TODO improve
            ok().render(createOrUpdatePetView)
        }
    }

    fun getUpdatePetView(request: ServerRequest): ServerResponse {
        petTypes = pets.findPetTypes()
        owner = owners.findById(request.pathVariable("ownerId").toInt())
        val pet = pets.findById(request.pathVariable("petId").toInt())
        return ok().render(createOrUpdatePetView, mapOf("pet" to pet, "types" to petTypes.map { it.name }, "owner" to owner))
    }

    fun updatePet(request: ServerRequest): ServerResponse {
        return try {
            val ownerId = request.pathVariable("ownerId").toInt()
            // val pet = request.form<Pet>() // TODO Fix this
            val pet = request.petFormParams(petTypes)
            pet.id = request.pathVariable("petId").toInt()
            pet.ownerId = ownerId

            when {
                pet.isValid() -> { // TODO remove validation if nullability is removed
                    pets.save(pet)
                    ok().render("redirect:/owners/${ownerId}")
                }
                else -> ok().render(createOrUpdatePetView, mapOf("pet" to pet, "types" to petTypes.map { it.name }, "owner" to owner))
            }
        } catch (e: IllegalArgumentException) {
            ok().render(createOrUpdatePetView)
        }
    }
}