package org.springframework.samples.petclinic.owner

import org.springframework.samples.petclinic.form
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
            ok().render(createOrUpdateOwnerView, mapOf("owner" to Owner()))

    fun addOwner(request: ServerRequest): ServerResponse {
        return try {
            val owner = request.form<Owner>()
            when {
                owner.isValid() -> {
                    owners.save(owner)
                    ok().render("redirect:/owners/${owner.id}")
                }
                else -> ok().render(createOrUpdateOwnerView, mapOf("owner" to owner))
            }
            owners.save(owner)
            ok().render("redirect:/owners/${owner.id}")
        } catch (e: IllegalArgumentException) { // Reflect error TODO improve
            ok().render(createOrUpdateOwnerView);
        }
    }

    fun findOwnerView(request: ServerRequest): ServerResponse =
            ok().render(findOwnerView, mapOf("owner" to Owner()))

    fun findOwnerByLastName(request: ServerRequest): ServerResponse {
        // TODO manage multiple fields error
        val search = request.params()["lastName"]?.firstOrNull() ?: ""
        val found = owners.findByLastName(search)
        return when {
            // TODO : Manage errors for thymeleaf (specific format ?) FieldError("lastName", "notFound", "not found")
            found.isEmpty() -> ok().render(findOwnerView, mapOf("owner" to Owner()))
            found.size == 1 -> ok().render("redirect:/owners/${found.first().id}")
            else -> ok().render(listOwnersView, mapOf("selections" to found, "owner" to Owner()))
        }
    }

    fun getOwnerUpdateView(request: ServerRequest): ServerResponse {
        val owner = owners.findById(request.pathVariable("ownerId").toInt())
        return ok().render(createOrUpdateOwnerView, mapOf("owner" to owner))
    }

    fun updateOwner(request: ServerRequest): ServerResponse {
        return try {
            val owner = request.form<Owner>()
            when {
                owner.isValid() -> {
                    owners.save(owner)
                    ok().render("redirect:/owners/${owner.id}")
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
        owner.pets = pets.findByOwnerId(ownerId)
        return ok().render(ownerDetailsView, mapOf("owner" to owner))
    }
}