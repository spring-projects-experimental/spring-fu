package com.sample

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class IntegrationTests {

    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

    private lateinit var context: ConfigurableApplicationContext

    @BeforeAll
    fun beforeAll() {
        context = app.run(profiles = "test")
    }

    @Test
    fun `Create a user successfully`() {
        client.post().uri("/api/user")
                .bodyValue(User("demo", "John", "Doe"))
                .exchange()
                .expectStatus().isOk
                .expectBody<User>().isEqualTo(User("demo", "John", "Doe"))
    }

    @Test
    fun `Empty fields request should fail`() {
        client.post().uri("/api/user")
                .bodyValue(User("", "", ""))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<Map<String, List<Map<String, *>>>>()
                .consumeWith { res ->
                    val details = res.responseBody!!.getValue("details")
                    Assertions.assertEquals(3, details.size)
                    Assertions.assertEquals("The size of \"login\" must be greater than or equal to 4. The given size is 0", details[0]["defaultMessage"])
                    Assertions.assertEquals("\"firstname\" must not be blank", details[1]["defaultMessage"])
                    Assertions.assertEquals("\"lastname\" must not be blank", details[2]["defaultMessage"])
                }
    }

    @AfterAll
    fun afterAll() {
        context.close()
    }
}