package com.sample

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

/**
 * @author Kevin Davin
 */
class IntegrationTests {

	private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

	private lateinit var context: ConfigurableApplicationContext

	@BeforeAll
	fun beforeAll() {
		context = app.run(profiles = "test")
	}

	@Test
	fun `request for users`() {
		client.get().uri("/api/user").exchange()
				.expectStatus().is2xxSuccessful
				.expectBody<String>()
				.isEqualTo("""[{"login":"smaldini","firstname":"Stéphane","lastname":"Maldini"},{"login":"sdeleuze","firstname":"Sébastien","lastname":"Deleuze"},{"login":"bclozel","firstname":"Brian","lastname":"Clozel"}]""")
	}

	@AfterAll
	fun afterAll() {
		context.close()
	}
}
