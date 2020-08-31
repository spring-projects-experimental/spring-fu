package org.springframework.samples.petclinic.pet

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.samples.petclinic.form
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok
import java.time.LocalDate

class PetHandler(private val pets: PetRepository, private val owners: OwnerRepository) {

    private val createOrUpdatePetView = "pets/createOrUpdatePetForm"

    // those vars are here to avoid multiple request to the db
    private lateinit var petTypes: List<PetType>
    private lateinit var owner: Owner

    fun getPetCreationView(request: ServerRequest): ServerResponse {
        petTypes = pets.findPetTypes()
        owner = owners.findById(request.pathVariable("ownerId").toInt())
        // Default value or VO that is the question
        return ok().render(createOrUpdatePetView, mapOf("pet" to PetView(),"types" to petTypes.map { it.name }, "owner" to owner))
    }

    fun addPet(request: ServerRequest): ServerResponse {
        return try {
            val ownerId = request.pathVariable("ownerId").toInt()
            val petView = request.form<PetView>()
            // TODO Validation petview
            val pet = petView.toPet(owner, petTypes)

            if (owner.getPet(pet.name) != null) {
                // TODO : Manage errors for thymeleaf (specific format ?) FieldError("name", "duplicate", "already exists")
                // Should be a reject values to mark as duplicate
                throw IllegalAccessException("duplicate")
            }

            owner.addPet(pet)
            pets.save(pet)
            ok().render("redirect:/owners/${ownerId}")

        } catch (e: IllegalArgumentException) { // Reflect error TODO improve
            ok().render(createOrUpdatePetView, mapOf("pet" to PetView(),"types" to petTypes.map { it.name }, "owner" to owner))
        }
    }

    fun getUpdatePetView(request: ServerRequest): ServerResponse {
        petTypes = pets.findPetTypes()
        owner = owners.findById(request.pathVariable("ownerId").toInt())
        val pet = pets.findById(request.pathVariable("petId").toInt())
        return ok().render(createOrUpdatePetView, mapOf("pet" to pet.toPetView(), "types" to petTypes.map { it.name }, "owner" to owner))
    }

    fun updatePet(request: ServerRequest): ServerResponse {
        return try {
            val ownerId = request.pathVariable("ownerId").toInt()

            val petView = request.form<PetView>()
            // TODO Validation petview
            val pet = petView.toPet(owner, petTypes)
                    .copy(id = request.pathVariable("petId").toInt())

            when {
                pet.isValid() -> {
                    pets.save(pet)
                    ok().render("redirect:/owners/${ownerId}")
                }
                else -> ok().render(createOrUpdatePetView, mapOf("pet" to pet.toPetView(), "types" to petTypes.map { it.name }, "owner" to owner))
            }
        } catch (e: IllegalArgumentException) {
            ok().render(createOrUpdatePetView)
        }
    }
}

class PetView {
    var id: Int? = null
    var name: String? = null
    @DateTimeFormat(pattern= "YYYY-MM-DD")
    var birthDate: LocalDate? = null
    var type: String? = null

    fun toPet(owner: Owner, types: List<PetType>): Pet =
            Pet(
                    name = name!!,
                    birthDate = birthDate!!,
                    owner = owner,
                    type = types.first { it.name == type!! }
            )

    fun isNew() = id == null
}

fun Pet.toPetView(): PetView = let {
        PetView().apply {
            id = it.id
            name = it.name
            birthDate = it.birthDate
            type = it.type.name
        }
}

fun Pet.isValid() = !name.isNullOrBlank()

fun ServerRequest.petFormParams(petTypes: List<PetType>, owner: Owner): Pet =
        Pet(
                name = param("name").orElseThrow { java.lang.IllegalArgumentException("name is mandatory") }.toString(),
                birthDate = LocalDate.parse(param("birthDate").orElseThrow { java.lang.IllegalArgumentException("birthDate is mandatory") }.toString()),
                type = petTypes.first { it.name == param("type").orElseThrow { java.lang.IllegalArgumentException("type is mandatory") }.toString() },
                owner = owner)