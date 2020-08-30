package org.springframework.samples.petclinic.model

import java.io.Serializable

open class BaseEntity: Serializable {

    var id: Int = 0

    fun isNew(): Boolean = id == 0
}