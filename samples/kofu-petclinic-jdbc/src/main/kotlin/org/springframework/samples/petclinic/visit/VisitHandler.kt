package org.springframework.samples.petclinic.visit

import org.springframework.samples.petclinic.form
import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok
import java.time.LocalDate

class VisitHandler(private val visits: VisitRepository, private val pets: PetRepository) {

    private val createOrUpdateVisitView = "pets/createOrUpdateVisitForm"

    fun getVisitsView(request: ServerRequest): ServerResponse {
        val petId = request.pathVariable("petId").toInt()
        val pet = pets.findById(petId)
        pet.visits = visits.findByPetId(petId)
        return ok().render(createOrUpdateVisitView, mapOf("pet" to pet, "visit" to Visit().apply{date = LocalDate.now()}))
    }

    fun addVisit(request: ServerRequest): ServerResponse {
        return try {
            val ownerId = request.pathVariable("ownerId").toInt()
            val visit = request.form<Visit>()
            when {
                visit.isValid() -> {
                    visits.save(visit)
                    ok().render("redirect:/owners/$ownerId")
                }
                else -> ok().render(createOrUpdateVisitView, mapOf("visit" to visit))
            }
        } catch (e: IllegalArgumentException) {
            ok().render(createOrUpdateVisitView)
        }
    }
}