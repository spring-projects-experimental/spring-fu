package com.sample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

public class IntegrationTests {

	private WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();

	private ConfigurableApplicationContext context;

	@BeforeAll
	public void beforeAll() {
		context = Application.app.run();
	}

	@Test
	public void requestRootEndpoint() {
		client.get().uri("/").exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(String.class).isEqualTo("Hello world!");
	}

	@AfterAll
	void afterAll() {
		context.close();
	}
}
