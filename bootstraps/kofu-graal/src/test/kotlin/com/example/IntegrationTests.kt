package com.examples

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import com.example.app
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class IntegrationTests {

	private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()

	@BeforeAll
	fun beforeAll() {
		app.run(profiles = "test")
	}

	@Test
	fun `Request string endpoint`() {
		client.get().uri("/").exchange()
				.expectStatus().is2xxSuccessful
				.expectBody<String>().isEqualTo("Hello GraalVM native images!")
	}

	@Test
	fun `Request json endpoint`() {
		client.get().uri("/api").exchange()
				.expectStatus().is2xxSuccessful
				.expectBody<String>().isEqualTo("{\"message\":\"Hello GraalVM native images!\"}")
	}

	@AfterAll
	fun afterAll() {
		app.stop()
	}
}