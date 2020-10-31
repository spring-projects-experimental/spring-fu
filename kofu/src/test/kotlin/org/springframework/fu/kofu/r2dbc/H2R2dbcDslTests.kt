package org.springframework.fu.kofu.r2dbc

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.test.StepVerifier
import java.nio.file.Files
import java.nio.file.Path

class H2R2dbcDslTests {

    @Test
    fun `enable r2dbc H2 embedded`(@TempDir tempDir: Path) {
        // Create a file for the db
        // Since if in memory each line execution not transactional will restart database ¯\_(ツ)_/¯
        val dbPath = tempDir.resolve("test.db")
        Files.createFile(dbPath)
        val app = application {
            r2dbc {
                url = "r2dbc:h2:file:///${dbPath.toAbsolutePath()}"
                transactional = true
            }
            beans {
                bean<TestRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<TestRepository>()

            StepVerifier
                    .create(repository.createTable()
                            .then(repository.save(user))
                            .then(repository.findById(user.id)))
                    .expectNext(user)
                    .verifyComplete()
            close()
        }
    }

    @Test
    fun `enable transactional`() {
        // Check that by default TransactionalOperator is not activated
        assertThrows<NoSuchBeanDefinitionException> {
            val app = application {
                r2dbc {
                    url = "r2dbc:h2:mem:///testdb"
                }
            }

            with(app.run()) {
                getBean<TransactionalOperator>()
            }
        }

        val app = application {
            r2dbc {
                url = "r2dbc:h2:mem:///testdb"
                transactional = true
            }
        }

        with(app.run()) {
            getBean<TransactionalOperator>()
        }
    }


    @Test
    fun `enable r2dbc H2 embedded transactional`() {
        val app = application {
            r2dbc {
                url = "r2dbc:h2:mem:///testdb"
                transactional = true
            }
            beans {
                bean<TestRepository>()
            }
        }

        with(app.run()) {
            val repository = getBean<TestRepository>()
            val transactionalOperator = getBean<TransactionalOperator>()

            StepVerifier
                    .create(
                            transactionalOperator.transactional(
                                    repository.createTable()
                                            .then(repository.save(user))
                                            .then(repository.findById(user.id))))
                    .expectNext(user)
                    .verifyComplete()
            close()
        }
    }
}

