package org.springframework.fu.kofu.r2dbc

import org.springframework.r2dbc.core.DatabaseClient
import java.io.Serializable

class TestRepository (private val dbClient: DatabaseClient) {
    fun createTable() = dbClient
            .sql("CREATE TABLE person (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255));")
            .then()

    fun findById(id: String) = dbClient
            .sql("SELECT * FROM person WHERE id = $id")
            .fetch()
            .one()
            .map{ values -> TestUser(values["id"] as String, values["name"] as String)}

    fun save(user: TestUser) = dbClient
            .sql("INSERT INTO person VALUES (${user.id}, '${user.name}')")
            .fetch()
            .rowsUpdated();
}

data class TestUser(val id: String, val name: String): Serializable