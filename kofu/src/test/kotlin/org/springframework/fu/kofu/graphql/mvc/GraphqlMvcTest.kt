package org.springframework.fu.kofu.graphql.mvc

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlRequest
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.graphql.MessageService
import org.springframework.fu.kofu.graphql.Mutation
import org.springframework.fu.kofu.graphql.Query
import org.springframework.fu.kofu.graphql.graphql
import org.springframework.fu.kofu.localServerPort
import org.springframework.fu.kofu.samples.gqlAddMessage
import org.springframework.fu.kofu.samples.gqlFindAll
import org.springframework.fu.kofu.samples.graphqlQuery
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.test.web.reactive.server.WebTestClient

class GraphqlMvcTest {

    private val app = application(WebApplicationType.SERVLET) {
        beans {
            bean { MessageService() }
        }
        webMvc {
            converters {
                jackson()
                string()
            }
            graphql {
                schema {
                    supportPackages = listOf("org.springframework.fu.kofu.graphql")
                    query {
                        Query(ref())
                    }
                    mutation {
                        Mutation(ref())
                    }
                }
            }
        }

    }

    @Test
    fun `Test mutate via graphql content type`() {
        val variables = mapOf("message" to mapOf("title" to "1", "content" to "2"))
        with(receiver = app.run()) {
            val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
            client.post()
                .uri("/graphql")
                .header("Content-type", "application/json")
                .syncBody(GraphqlRequest(gqlAddMessage(), "addMessage", variables))
                .exchange()
            client.post()
                .uri("/graphql")
                .header("Content-type", "application/graphql")
                .syncBody(gqlFindAll())
                .exchange()
                .expectBody()
                .jsonPath("data.messages[0].title").isEqualTo("1")
            close()
        }
    }

    @Test
    fun `Test query with GET request`() {
        val query = gqlFindAll()
        val variables = mapOf("message" to mapOf("title" to "1", "content" to "2"))
        with(receiver = app.run()) {
            val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
            client.post()
                .uri("/graphql")
                .header("Content-type", "application/json")
                .syncBody(GraphqlRequest(gqlAddMessage(), "addMessage", variables))
                .exchange()
            client.get()
                .uri("/graphql?query={query}&operationName={operationName}", query, "findAll")
                .header("Content-type", "application/json")
                .exchange()
                .expectBody()
                .jsonPath("data.messages[0].title").isEqualTo("1")
            close()
        }
    }

    @Test
    fun `Test mutate data with GET request`() {
        val query = gqlAddMessage()
        val variables = mapOf("message" to mapOf("title" to "1", "content" to "2"))
        with(receiver = app.run()) {
            val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
            val mapper = getBean<ObjectMapper>()
            client.get()
                .uri("/graphql?query={query}&operationName={operationName}&variables={variables}", query, "addMessage", mapper.writeValueAsString(variables))
                .header("Content-type", "application/json")
                .exchange()
                .expectBody()
                .jsonPath("errors[0].message").isEqualTo("Validation error of type FieldUndefined: Field 'save' in type 'Mutation' is undefined @ 'save'")
            close()
        }
    }

    @Test
    fun `Test mutate and query with aliases`() {
        val query = graphqlQuery()
        with(app.run()) {
            val client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
            val variables = mapOf("message" to mapOf("title" to "1", "content" to "2"))
            client.post()
                .uri("/graphql")
                .header("Content-type", "application/json")
                .syncBody(GraphqlRequest(query, "addMessage", variables))
                .exchange()
            client.post()
                .uri("/graphql")
                .header("Content-type", "application/json")
                .syncBody(GraphqlRequest(query, "findAll"))
                .exchange()
                .expectBody()
                .jsonPath("data.messages[0].title").isEqualTo("1")
            close()
        }
    }
}