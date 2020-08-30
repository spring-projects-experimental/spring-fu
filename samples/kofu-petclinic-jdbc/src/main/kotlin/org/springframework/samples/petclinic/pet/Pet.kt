package org.springframework.samples.petclinic.pet

import org.springframework.samples.petclinic.model.NamedEntity
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.visit.Visit
import java.time.LocalDate

class Pet: NamedEntity(){

    var birthDate: LocalDate? = null
    var type: PetType? = null
    var owner: Owner? = null
    var visits: Set<Visit> = linkedSetOf()
    var typeId: Int = 0
    var ownerId: Int = 0

    fun getVisitsSorted(): Set<Visit> =
            visits.sortedBy { it.date }.toHashSet()

    fun addVisit(visit: Visit) {
        visit.petId = this.id
        visits += visit
    }

    fun isValid() = !name.isNullOrBlank() &&
            birthDate != null
            && typeId != null && typeId != 0
            && ownerId != null && ownerId != 0

}
