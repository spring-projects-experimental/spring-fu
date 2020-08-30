package org.springframework.samples.petclinic.visit

import org.springframework.samples.petclinic.model.BaseEntity
import java.time.LocalDate

class Visit : BaseEntity() {

    var date: LocalDate? = null
    var description: String? = null
    var petId: Int? = null

    fun isValid(): Boolean {
        return petId != 0 && petId != null && date != null
    }
}