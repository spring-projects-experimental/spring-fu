package com.sample

import kotlinx.coroutines.runBlocking
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
		context = dataApp.run(profiles = "test")
	}

	@Test
	fun count() {
		val repository = context.getBean<UserRepository>()
		runBlocking {
			assertEquals(3, repository.count())
		}
	}

	@AfterAll
	fun afterAll() {
		context.close()
	}
}
