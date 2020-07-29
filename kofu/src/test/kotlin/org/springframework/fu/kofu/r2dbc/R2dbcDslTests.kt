package org.springframework.fu.kofu.r2dbc

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.transaction.reactive.TransactionalOperator
import org.testcontainers.containers.GenericContainer
import reactor.test.StepVerifier
import java.io.Serializable

class RedisDslTests {

	@Test
	fun `enable r2dbc H2 embedded`() {
		val app = application {
			r2dbc {
				url = "r2dbc:h2:mem:///testdb"
			}
			beans {
				bean<TestRepository>()
			}
		}

		with(app.run()) {
			val repository = getBean<TestRepository>()

			StepVerifier
					.create(repository.createTable()
							.flatMap { repository.save(TestUser("1", "foo")) }
							.flatMap { repository.findById("1") })
					.expectNext(TestUser("1", "foo"))
			close()
		}
	}

	@Test
	fun `enable r2dbc Postgres`() {
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
			r2dbc {
				url = "r2dbc:postgresql://${pg.containerIpAddress}:${pg.firstMappedPort}/db"
				username = "jo"
				password = "pwd"
			}
			beans {
				bean<TestRepository>()
			}
		}

		with(app.run()) {
			val repository = getBean<TestRepository>()

			StepVerifier
					.create(repository.createTable()
							.flatMap { repository.save(TestUser("1", "foo")) }
							.flatMap { repository.findById("1") })
					.expectNext(TestUser("1", "foo"))
			close()
		}

		pg.stop()
	}

	@Test
	fun `enable trasactional`() {
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
}

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