package org.springframework.fu.kofu.r2dbc

import org.springframework.r2dbc.core.DatabaseClient
import java.io.Serializable
import java.util.*

class TestRepository (private val dbClient: DatabaseClient) {
    fun createTable() = dbClient
            .sql("CREATE TABLE person (id UUID PRIMARY KEY, name VARCHAR(255));")
            .then()

    fun findById(id: UUID) = dbClient
            .sql("SELECT * FROM person WHERE id = :id")
            .bind("id", id)
            // strange enough fetch().map { it -> ... } actually compiles and runs,
            // but produces unexpected results, like access by key can give value of other key
            .map { row, _ -> TestUser(row["id"] as UUID, row["name"] as String) }
            .one()

    fun save(user: TestUser) = dbClient
            .sql("INSERT INTO person(id, name) VALUES (:id, :name)")
            .bind("id", user.id)
            .bind("name", user.name)
            .fetch()
            .rowsUpdated();
}

val user = TestUser(UUID.randomUUID(), "foo")
data class TestUser(val id: UUID, val name: String): Serializable
