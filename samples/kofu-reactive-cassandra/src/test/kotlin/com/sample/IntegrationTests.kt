package com.sample

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.testcontainers.containers.CassandraContainer

class IntegrationTests {

	private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

	private lateinit var context: ConfigurableApplicationContext

	class KCassandraContainer : CassandraContainer<KCassandraContainer>() // https://github.com/testcontainers/testcontainers-java/issues/318
	private val cassandraContainer = KCassandraContainer().withInitScript("schema.cql")

	@BeforeAll
	fun beforeAll() {
		cassandraContainer.start()
		val properties = ApplicationProperties(
			cassandraPort = cassandraContainer.firstMappedPort,
			cassandraHost = cassandraContainer.containerIpAddress,
			serverPort = 8181
		)
		context = app(properties).run()
	}

	@Test
	fun `Request HTML endpoint`() {
		cassandraContainer.start()
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

	@Test
	fun `Request conf endpoint`() {
		client.get().uri("/conf").exchange()
			.expectStatus().is2xxSuccessful
			.expectHeader().contentType("text/plain;charset=UTF-8")
	}

	@AfterAll
	fun afterAll() {
		context.close()
		cassandraContainer.stop()
	}
}