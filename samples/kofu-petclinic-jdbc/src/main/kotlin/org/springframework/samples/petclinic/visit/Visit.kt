package org.springframework.samples.petclinic.visit

import java.time.LocalDate

data class Visit(
        val id: Int? = null,
        val date: LocalDate,
        val description: String
) {
    fun isNew() = id == null
}