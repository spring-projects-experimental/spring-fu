package org.springframework.fu.kofu.jooq

import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.jooq.impl.SQLDataType
import java.io.Serializable
import java.util.*

/**
 * @author Kevin Davin
 */
class JooqRepository(private val query: DSLContext) {

    private val person = table("person")
    private val id = field("id", SQLDataType.UUID)
    private val name = field("name", SQLDataType.VARCHAR(255))

    fun createTable() {

        query.createTable(person)
                .columns(id, name)
                .constraint(constraint("pk_person").primaryKey(id))
                .execute()
    }

    fun findById(userID: UUID): JooqUser {
        return query
                .select(id, name)
                .from(person)
                .where(id.equal(userID))
                .fetchOne { (id, name) -> JooqUser(id, name) }
                ?: error("User with id $userID not found")
    }

    fun save(user: JooqUser) {
        query
                .insertInto(person)
                .set(id, user.id)
                .set(name, user.name)
                .execute()
    }

}

val user = JooqUser(UUID.randomUUID(), "foo")

data class JooqUser(val id: UUID, val name: String) : Serializable
