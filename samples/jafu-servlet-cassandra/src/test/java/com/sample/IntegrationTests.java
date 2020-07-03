package com.sample;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

class IntegrationTests {

    private final WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build();

    private ConfigurableApplicationContext context;

    @BeforeAll
    public void beforeAll() {
        Application.main(new String[]{"--spring.profiles.active=test"});
    }

    @Test
    void testSaveUser() {
        final User user = new User("Hamid", "M");

        client.post().uri("/users").bodyValue(user).exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Hamid")
                .jsonPath("$.lastName").isEqualTo("M");
    }

    @Test
    void testFindAllUsers() {
        client.get().uri("/users").exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(User.class);
    }

    @Test
    void testFindOneUser() {
        client.get().uri("/users/" + UUID.randomUUID().toString()).exchange()
                .expectStatus().isNotFound();
    }

}
