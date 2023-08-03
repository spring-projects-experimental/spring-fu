package org.springframework.fu.kofu.jooq

import org.jooq.DSLContext
import org.junit.Assert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.jdbc.DataSourceType
import org.testcontainers.containers.GenericContainer

/**
 * @author Kevin Davin
 */
class JooqDslTest {

    @Test
    fun `enable jooq`() {
        val app = application {
            jooq(DataSourceType.Embedded)
        }

        with(app.run()) {
            val query = getBean<DSLContext>()
            Assert.assertNotNull(query)
            close()
        }
    }

    @Test
    fun `enable jooq with postgres`() {
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
            jooq(DataSourceType.Generic) {
                url = "jdbc:postgresql://${pg.containerIpAddress}:${pg.firstMappedPort}/db"
                username = "jo"
                password = "pwd"
            }
            beans {
                bean<JooqRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<JooqRepository>()

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
            jooq(DataSourceType.Hikari) {
                url = "jdbc:postgresql://${pg.containerIpAddress}:${pg.firstMappedPort}/db"
                username = "jo"
                password = "pwd"
            }
            beans {
                bean<JooqRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<JooqRepository>()

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
            jooq(DataSourceType.Embedded) {
                url = "jdbc::h2:mem:///testdb"
                username = "jo"
                password = "pwd"
            }
            beans {
                bean<JooqRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<JooqRepository>()

            repository.createTable()
            repository.save(user)
            val actual = repository.findById(user.id)
            assertEquals(user, actual)
            close()
        }
    }
}
