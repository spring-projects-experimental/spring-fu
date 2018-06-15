package org.springframework.fu.sample.coroutines

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@EnabledIfSystemProperty(named = "mongo.server", matches = "true")
class IntegrationTests {

	private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

	@BeforeAll
	fun beforeAll() {
		app.run(profiles = "test")
	}

	@Test
	fun `Request HTML endpoint`() {
		client.get().uri("/").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType("text/html;charset=UTF-8")
	}

	@Test
	fun `Request HTTP API endpoint`() {
		client.get().uri("/api/user").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
	}

	@Test
	fun `Request conf endpoint`() {
		client.get().uri("/conf").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType("text/plain;charset=UTF-8")
	}

	@AfterAll
	fun afterAll() {
		app.stop()
	}
}