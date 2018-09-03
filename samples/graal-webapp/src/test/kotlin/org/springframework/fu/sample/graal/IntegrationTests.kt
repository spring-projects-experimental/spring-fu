package org.springframework.fu.sample.graal

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.*
import org.springframework.test.web.reactive.server.WebTestClient

class IntegrationTests {

	private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()

	@BeforeAll
	fun beforeAll() {
		app.run(profiles = "test")
	}

	@Test
	fun `Request root endpoint`() {
		client.get().uri("/").exchange()
				.expectStatus().is2xxSuccessful
				.expectBody<String>().isEqualTo("Hello world!")
	}

	@AfterAll
	fun afterAll() {
		app.stop()
	}
}