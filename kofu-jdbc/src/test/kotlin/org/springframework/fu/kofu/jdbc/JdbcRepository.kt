package org.springframework.fu.kofu.jdbc

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.Serializable
import java.sql.PreparedStatement
import java.util.*

class JdbcRepository(private val jdbcTemplate: JdbcTemplate) {

    private val namedJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

    fun createTable() = jdbcTemplate.execute("CREATE TABLE person (id UUID PRIMARY KEY, name VARCHAR(255));")

    fun findById(id: UUID): TestUser =
            namedJdbcTemplate.query("SELECT * FROM person WHERE id = :id", MapSqlParameterSource().addValue("id", id)) {
                rs, _ -> TestUser(id, rs.getString(2))
            }.first()

    fun save(user: TestUser) =
            namedJdbcTemplate
                    .execute("INSERT INTO person(id, name) VALUES (:id, :name)",
                            MapSqlParameterSource()
                                    .addValue("id", user.id)
                                    .addValue("name", user.name),
                            PreparedStatement::execute)
}

val user = TestUser(UUID.randomUUID(), "foo")

data class TestUser(val id: UUID, val name: String) : Serializable
