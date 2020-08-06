package org.springframework.fu.kofu.r2dbc

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer
import reactor.test.StepVerifier

class PostgreSqlR2dbcDslTests {

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
							.then(repository.save(user))
							.then(repository.findById(user.id)))
					.expectNext(user)
					.verifyComplete()
			close()
		}

		pg.stop()
	}
}
