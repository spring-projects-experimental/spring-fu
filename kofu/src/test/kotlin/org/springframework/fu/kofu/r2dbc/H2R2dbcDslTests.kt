package org.springframework.fu.kofu.r2dbc

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.test.StepVerifier

class H2R2dbcDslTests {

	@Test
	fun `enable r2dbc H2 embedded`() {
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
					.create(repository.createTable()
							.then(repository.save(user))
							.then(repository.findById(user.id))
							// another option would be to plug in SingleConnectionFactory somehow
							// because in memory (serverless) h2 databases don't seem to be shared between connections
							.`as`(transactionalOperator::transactional)
					)
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
}

