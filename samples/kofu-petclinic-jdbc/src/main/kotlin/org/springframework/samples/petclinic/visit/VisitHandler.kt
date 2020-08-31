package org.springframework.samples.petclinic.visit

import org.springframework.samples.petclinic.form
import org.springframework.samples.petclinic.pet.Pet
import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.samples.petclinic.pet.PetType
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VisitHandler(private val visits: VisitRepository, private val pets: PetRepository) {

    private val createOrUpdateVisitView = "pets/createOrUpdateVisitForm"

    fun getVisitsView(request: ServerRequest): ServerResponse {
        val petId = request.pathVariable("petId").toInt()
        val pet = pets.findById(petId)
                .copy(visits = visits.findByPetId(petId))
        return ok().render(createOrUpdateVisitView, mapOf("pet" to pet, "visit" to VisitView()))
    }

    fun addVisit(request: ServerRequest): ServerResponse {
        val ownerId = request.pathVariable("ownerId").toInt()
        val petId = request.pathVariable("petId").toInt()
        return try {
            val visit = request.form<VisitView>()
            // TODO validation view
            visits.save(visit.toVisit(), petId)
            ok().render("redirect:/owners/$ownerId")
        } catch (e: IllegalArgumentException) {
            val pet = pets.findById(petId)
            ok().render(createOrUpdateVisitView, mapOf("pet" to pet, "visit" to Visit(date = LocalDate.now(), description = "")))
        }
    }
}


// Make thymeleaf happy
// Use to have some object for template and add nullable values
class VisitView {
    var id: Int? = null
    var date: LocalDate? = null
    var description: String? = null

    fun isNew() = id == null

    fun toVisit(): Visit =
        Visit(
                id = id,
                description = description!!,
                date = date!!
        )
}

fun ServerRequest.visitForm(): Visit =
        Visit(
                date = LocalDate.parse(param("date").orElseThrow { java.lang.IllegalArgumentException("birthDate is mandatory") }.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                description = param("description").orElseThrow { java.lang.IllegalArgumentException("description is mandatory") }.toString()
        )