package org.springframework.fu.kofu.jdbc

import org.junit.Assert
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.GenericContainer

class JdbcDslTests {

    @Test
    fun `enable jdbc`() {
        val app = application {
            jdbc()
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
            jdbc {
                url = "postgresql://${pg.containerIpAddress}:${pg.firstMappedPort}/db"
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

}