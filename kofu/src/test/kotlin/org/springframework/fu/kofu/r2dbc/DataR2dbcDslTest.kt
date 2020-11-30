package org.springframework.fu.kofu.r2dbc

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.getBean
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer
import reactor.test.StepVerifier
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class DataR2dbcDslTest {

    @Test
    fun `enable data with h2 embedded r2dbc`(@TempDir tempDir: Path) {
        // Create a file for the db
        // Since if in memory each line execution not transactional will restart database ¯\_(ツ)_/¯
        val dbPath = tempDir.resolve("test.db")
        Files.createFile(dbPath)

        val app = application {
            dataR2dbc {
                r2dbc {
                    url = "r2dbc:h2:file:///${dbPath.toAbsolutePath()}"
                }
            }
        }

        with(app.run()) {
            val entityTemplate = getBean<R2dbcEntityTemplate>()
            Assert.assertNotNull(entityTemplate)

            StepVerifier
                    .create(entityTemplate.databaseClient.sql("CREATE TABLE TEST_DATA_USER (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                            .then(entityTemplate.insert(dataUser))
                            .then(entityTemplate.selectOne(Query.query(Criteria.where("id").isEqual(dataUser.id)), TestDataUser::class.java)))
                    .expectNext(dataUser)

                    .verifyComplete()
            close()
        }
    }

    @Test
    fun `enable data with postgres r2dbc`() {
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
            dataR2dbc {
                r2dbc {
                    url = "r2dbc:postgresql://${pg.containerIpAddress}:${pg.firstMappedPort}/db"
                    username = "jo"
                    password = "pwd"
                }
            }
        }

        with(app.run()) {
            val entityTemplate = getBean<R2dbcEntityTemplate>()
            Assert.assertNotNull(entityTemplate)

            StepVerifier
                    .create(entityTemplate.databaseClient.sql("CREATE TABLE TEST_DATA_USER (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                            .then(entityTemplate.insert(dataUser))
                            .then(entityTemplate.selectOne(Query.query(Criteria.where("id").isEqual(dataUser.id)), TestDataUser::class.java)))
                    .expectNext(dataUser)

                    .verifyComplete()
            close()
        }
        pg.stop()
    }
}

val dataUser = TestDataUser(UUID.randomUUID(), "foo")
data class TestDataUser(@Id val id: UUID, val name: String): Serializable

