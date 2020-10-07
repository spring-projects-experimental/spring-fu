package com.sample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class IntegrationTests {

    private WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build();

    private ConfigurableApplicationContext context;

    @BeforeAll
    public void beforeAll() {
        context = Application.app.run("test");
    }

    @Test
    public void requestHttpApiEndpoint() {
        client.get().uri("/api/users").exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);

    }

    @Test
    public void requestHtmlEndpoint() {
        client.get().uri("/").exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType("text/html;charset=UTF-8");
    }

    @Test
    public void requestConfEndpoint() {
        client.get().uri("/conf").exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType("text/plain;charset=UTF-8");
    }

    @AfterAll
    void afterAll() {
        context.stop();
    }
}
