package org.springframework.fu.kofu.jdbc

import org.springframework.jdbc.core.JdbcTemplate
import java.io.Serializable
import java.util.*

class JdbcRepository(private val jdbcTemplate: JdbcTemplate) {

    fun createTable() = jdbcTemplate.execute("CREATE TABLE person (id UUID PRIMARY KEY, name VARCHAR(255));")

    fun findById(id: UUID): TestUser =
            jdbcTemplate.query("SELECT * FROM person WHERE id = \"$id\"")
            {
                rs, _ -> TestUser(id, rs.getString(2))
            }.first()

    fun save(user: TestUser) =
            jdbcTemplate.execute("""INSERT INTO person(id, name) VALUES ("${user.id}", "${user.name}")""")
}

val user = TestUser(UUID.randomUUID(), "foo")

data class TestUser(val id: UUID, val name: String) : Serializable
