package com.sample

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.application

class UserRepositoryTests {

	private val dataApp = application {
		enable(dataConfig)
	}

	private lateinit var context: ConfigurableApplicationContext

	@BeforeAll
	fun beforeAll() {
		context = app.run(profiles = "test")
	}

	@Test
	fun count() {
		val repository = context.getBean<UserRepository>()
		assertEquals(3, repository.count().block())
	}

	@AfterAll
	fun afterAll() {
		context.close()
	}
}
