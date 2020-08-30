package org.springframework.samples.petclinic.model

open class NamedEntity(
): BaseEntity() {
    var name: String? = null
}