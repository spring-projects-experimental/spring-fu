package com.sample

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.testcontainers.containers.GenericContainer

class IntegrationTests {

	private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

	private lateinit var context: ConfigurableApplicationContext

	private val redisContainer = object : GenericContainer<Nothing>("redis:5") {}

	@BeforeAll
	fun beforeAll() {
		redisContainer.withExposedPorts(6379)
		redisContainer.start()
		val properties = ApplicationProperties(
				redisPort = redisContainer.firstMappedPort,
				redisHost = redisContainer.containerIpAddress,
				serverPort = 8181
		)
		context = app(properties).run()
	}

	@Test
	fun `Request root endpoint`() {
		client.get().uri("/").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType("text/html;charset=UTF-8")
	}

	@Test
	fun `Request HTTP API endpoint`() {
		client.get().uri("/api/user").exchange()
				.expectStatus().is2xxSuccessful
				.expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
				.expectBodyList<User>()
				.hasSize(3)
	}

	@AfterAll
	fun afterAll() {
		context.close()
		redisContainer.stop()
	}
}