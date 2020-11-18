package org.springframework.fu.kofu.jdbc

import org.junit.Assert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testcontainers.containers.GenericContainer
import java.sql.PreparedStatement

class JdbcDslTests {

    @Test
    fun `enable jdbc`() {
        val app = application {
            jdbc(DataSourceType.Embedded)
        }

        with(app.run()) {
            val jdbcTemplate = getBean<JdbcTemplate>()
            Assert.assertNotNull(jdbcTemplate)
            close()
        }
    }


    @Test
    fun `enable jdbc Postgres`() {
        val pg = object : GenericContainer<Nothing>("postgres:13") {
            init {
                withExposedPorts(5432)
                withEnv("POSTGRES_USER", "jo")
                withEnv("POSTGRES_PASSWORD", "pwd")
                withEnv("POSTGRES_DB", "db")
            }
        }
        pg.start()

        val app = application {
            jdbc(DataSourceType.Generic) {
                url = "jdbc:postgresql://${pg.containerIpAddress}:${pg.firstMappedPort}/db"
                username = "jo"
                password = "pwd"
            }
            beans {
                bean<JdbcRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<JdbcRepository>()

            repository.createTable()
            repository.save(user)
            val actual = repository.findById(user.id)
            assertEquals(user, actual)
            close()
        }

        pg.stop()
    }

    @Test
    fun `enable jdbc Postgres using Hikari datasource`() {
        val pg = object : GenericContainer<Nothing>("postgres:13") {
            init {
                withExposedPorts(5432)
                withEnv("POSTGRES_USER", "jo")
                withEnv("POSTGRES_PASSWORD", "pwd")
                withEnv("POSTGRES_DB", "db")
            }
        }
        pg.start()

        val app = application {
            jdbc(DataSourceType.Hikari) {
                url = "jdbc:postgresql://${pg.containerIpAddress}:${pg.firstMappedPort}/db"
                username = "jo"
                password = "pwd"
            }
            beans {
                bean<JdbcRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<JdbcRepository>()

            repository.createTable()
            repository.save(user)
            val actual = repository.findById(user.id)
            assertEquals(user, actual)
            close()
        }

        pg.stop()
    }

    @Test
    fun `enable jdbc H2 embedded datasource`() {
        val app = application {
            jdbc(DataSourceType.Embedded) {
                url = "jdbc::h2:mem:///testdb"
                username = "jo"
                password = "pwd"
            }
            beans {
                bean<JdbcRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<JdbcRepository>()

            repository.createTable()
            repository.save(user)
            val actual = repository.findById(user.id)
            assertEquals(user, actual)
            close()
        }
    }

    @Test
    fun `enable jdbc MySQL`() {
        val mysql = object : GenericContainer<Nothing>("mysql:8") {
            init {
                withExposedPorts(3306)
                withEnv("MYSQL_USER", "jo")
                withEnv("MYSQL_PASSWORD", "pwd")
                withEnv("MYSQL_ROOT_PASSWORD", "root")
                withEnv("MYSQL_DATABASE", "db")
            }
        }
        mysql.start()

        val app = application {
            jdbc(DataSourceType.Generic) {
                url = "jdbc:mysql://${mysql.containerIpAddress}:${mysql.firstMappedPort}/db"
                username = "jo"
                password = "pwd"
            }
        }

        with(app.run()) {
            val jdbcTemplate = getBean<JdbcTemplate>()
            val namedJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

            // Not using JdbcRepository due to MySQL (non support of UUID type)
            jdbcTemplate.execute("CREATE TABLE person (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255));")
            namedJdbcTemplate
                    .execute("INSERT INTO person(id, name) VALUES (:id, :name)",
                            MapSqlParameterSource()
                                    .addValue("id", user.id.toString())
                                    .addValue("name", user.name),
                            PreparedStatement::execute)
            val actual = namedJdbcTemplate.query("SELECT * FROM person WHERE id = :id", MapSqlParameterSource().addValue("id", user.id.toString())) {
                rs, _ -> TestUser(user.id, rs.getString(2))
            }.first()
            assertEquals(user, actual)
            close()
        }

        mysql.stop()
    }

    @Test
    fun `enable jdbc MySQL with Hikari`() {
        val mysql = object : GenericContainer<Nothing>("mysql:8") {
            init {
                withExposedPorts(3306)
                withEnv("MYSQL_USER", "jo")
                withEnv("MYSQL_PASSWORD", "pwd")
                withEnv("MYSQL_ROOT_PASSWORD", "root")
                withEnv("MYSQL_DATABASE", "db")
            }
        }
        mysql.start()

        val app = application {
            jdbc(DataSourceType.Hikari) {
                url = "jdbc:mysql://${mysql.containerIpAddress}:${mysql.firstMappedPort}/db"
                username = "jo"
                password = "pwd"
            }
        }

        with(app.run()) {
            val jdbcTemplate = getBean<JdbcTemplate>()
            val namedJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

            // Not using JdbcRepository due to MySQL (non support of UUID type)
            jdbcTemplate.execute("CREATE TABLE person (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255));")
            namedJdbcTemplate
                    .execute("INSERT INTO person(id, name) VALUES (:id, :name)",
                            MapSqlParameterSource()
                                    .addValue("id", user.id.toString())
                                    .addValue("name", user.name),
                            PreparedStatement::execute)
            val actual = namedJdbcTemplate.query("SELECT * FROM person WHERE id = :id", MapSqlParameterSource().addValue("id", user.id.toString())) {
                rs, _ -> TestUser(user.id, rs.getString(2))
            }.first()
            assertEquals(user, actual)
            close()
        }

        mysql.stop()
    }

}