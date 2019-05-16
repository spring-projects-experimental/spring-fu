package org.springframework.fu.kofu.graphql.reactive

import org.junit.jupiter.api.Test
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlRequest
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.graphql.MessageService
import org.springframework.fu.kofu.graphql.Mutation
import org.springframework.fu.kofu.graphql.Query
import org.springframework.fu.kofu.graphql.graphql
import org.springframework.fu.kofu.localServerPort
import org.springframework.fu.kofu.samples.graphqlQuery
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.test.web.reactive.server.WebTestClient

class GraphqlReactiveTest {

    private val app = application(WebApplicationType.REACTIVE) {
        beans {
            bean { MessageService() }
        }
        webFlux {
            codecs {
                string()
                jackson()
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