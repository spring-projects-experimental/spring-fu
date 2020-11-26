package com.sample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class IntegrationTests {

	private WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build();

	private ConfigurableApplicationContext context;

	@BeforeAll
	void beforeAll() {
		context = Application.app.run("test");
	}

	@Test
	void requestHttpApiEndpoint() {
		client.get().uri("/").exchange()
				.expectStatus().is2xxSuccessful()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	public void requestHttpApiEndpointForListingAllUsers() {
		client.get().uri("/api/user").exchange()
				.expectStatus().is2xxSuccessful()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
				.expectBodyList(User.class)
				.hasSize(3);
	}

	@Test
	public void requestHttpApiEndpointForGettingOneSpecificUser() {
		client.get().uri("/api/user/bclozel").exchange()
				.expectStatus().is2xxSuccessful()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
				.expectBody(User.class)
				.isEqualTo(new User("bclozel", "Brian", "Clozel"));
	}

	@AfterAll
	void afterAll() {
		context.stop();
	}
}
