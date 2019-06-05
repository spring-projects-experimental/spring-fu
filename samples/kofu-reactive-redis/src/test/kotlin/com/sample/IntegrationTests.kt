package com.sample

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

class IntegrationTests {

	private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

	private lateinit var context: ConfigurableApplicationContext

	@BeforeAll
	fun beforeAll() {
		context = app.run(profiles = "test")
	}

	@Test
	fun `Request root endpoint`() {
		client.get().uri("/").exchange()
				.expectStatus().is2xxSuccessful
				.expectBody<String>().isEqualTo("Hello world!")
	}

	@Test
	fun `Request HTTP API endpoint`() {
		client.get().uri("/api/user").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
				.expectBodyList<User>()
				.hasSize(3)
	}

	@Test
	fun `Request API endpoint`() {
		client.get().uri("/api").exchange()
				.expectStatus().is2xxSuccessful
				.expectBody<String>().isEqualTo("{\"message\":\"Hello world!\"}")
	}

	@AfterAll
	fun afterAll() {
		context.close()
	}
}