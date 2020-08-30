package org.springframework.samples.petclinic.model

import org.springframework.dao.DataRetrievalFailureException
import kotlin.jvm.Throws


@Throws(DataRetrievalFailureException::class)
fun <T: BaseEntity> getById(entities: Collection<T>, entityId: Int): T =
        entities.first { it.id == entityId}