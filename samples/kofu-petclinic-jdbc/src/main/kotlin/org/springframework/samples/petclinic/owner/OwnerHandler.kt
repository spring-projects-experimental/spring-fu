package org.springframework.samples.petclinic.owner

import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok

class OwnerHandler(private val owners: OwnerRepository, private val pets: PetRepository) {

    private val createOrUpdateOwnerView = "owners/createOrUpdateOwnerForm"
    private val findOwnerView = "owners/findOwners"
    private val listOwnersView = "owners/ownersList"
    private val ownerDetailsView = "owners/ownerDetails"

    fun getOwnerCreationView(request: ServerRequest): ServerResponse =
            ok().render(createOrUpdateOwnerView, mapOf("owner" to Owner.default()))

    fun addOwner(request: ServerRequest): ServerResponse {
        return try {
            val owner = request.ownerFormParams()
            when {
                owner.isValid() -> {
                    val savedOwner = owners.save(owner)
                    ok().render("redirect:/owners/${savedOwner.id}")
                }
                else -> ok().render(createOrUpdateOwnerView, mapOf("owner" to owner))
            }
        } catch (e: IllegalArgumentException) { // Reflect error TODO improve
            ok().render(createOrUpdateOwnerView);
        }
    }

    fun findOwnerView(request: ServerRequest): ServerResponse =
            ok().render(findOwnerView, mapOf("owner" to Owner.default()))

    fun findOwnerByLastName(request: ServerRequest): ServerResponse {
        val search = request.params()["lastName"]?.firstOrNull() ?: ""
        val found = owners.findByLastName(search)
        return when {
            // TODO : Manage errors for thymeleaf (specific format ?) FieldError("lastName", "notFound", "not found")
            found.isEmpty() -> ok().render(findOwnerView, mapOf("owner" to Owner.default()))
            found.size == 1 -> ok().render("redirect:/owners/${found.first().id}")
            else -> ok().render(listOwnersView, mapOf("selections" to found))
        }
    }

    fun getOwnerUpdateView(request: ServerRequest): ServerResponse {
        val owner = owners.findById(request.pathVariable("ownerId").toInt())
        return ok().render(createOrUpdateOwnerView, mapOf("owner" to owner))
    }

    fun updateOwner(request: ServerRequest): ServerResponse {
        return try {
            val owner = request.ownerFormParams()
                    .copy(id= request.pathVariable("ownerId").toInt())
            when {
                owner.isValid() -> {
                    val savedOwner = owners.save(owner)
                    ok().render("redirect:/owners/${savedOwner.id}")
                }
                else -> ok().render(createOrUpdateOwnerView, mapOf("owner" to owner))
            }
        } catch (e: IllegalArgumentException) {
            ok().render(createOrUpdateOwnerView)
        }
    }

    fun getOwnerById(request: ServerRequest): ServerResponse {
        val ownerId = request.pathVariable("ownerId").toInt()
        val owner = owners.findById(ownerId)
                .copy(pets = pets.findByOwnerId(ownerId))
        return ok().render(ownerDetailsView, mapOf("owner" to owner))
    }
}

fun Owner.isValid() = firstName.isNotBlank()
        && lastName.isNotBlank()
        && address.isNotBlank()
        && city.isNotBlank()
        && telephone.isNotBlank()

fun Owner.Companion.default(): Owner =
        Owner(
                firstName = "",
                lastName = "",
                telephone = "",
                city = "",
                address = ""
        )

fun ServerRequest.ownerFormParams(): Owner =
        Owner(
                firstName = param("firstName").orElseThrow { java.lang.IllegalArgumentException("firstName is mandatory") }.toString(),
                lastName = param("lastName").orElseThrow { java.lang.IllegalArgumentException("lastName is mandatory") }.toString(),
                address = param("address").orElseThrow { java.lang.IllegalArgumentException("address is mandatory") }.toString(),
                city = param("city").orElseThrow { java.lang.IllegalArgumentException("city is mandatory") }.toString(),
                telephone = param("telephone").orElseThrow { java.lang.IllegalArgumentException("telephone is mandatory") }.toString())