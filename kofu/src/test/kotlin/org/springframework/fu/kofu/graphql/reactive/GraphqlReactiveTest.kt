package org.springframework.fu.kofu.graphql.reactive

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlRequest
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.graphql.*
import org.springframework.fu.kofu.localServerPort
import org.springframework.fu.kofu.samples.gqlAddMessage
import org.springframework.fu.kofu.samples.gqlObserveMessage
import org.springframework.fu.kofu.samples.graphqlQuery
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import java.net.URI


class GraphqlReactiveTest {

    private val app = application(WebApplicationType.REACTIVE) {
        beans {
            bean<RequestUpgradeStrategy> { ReactorNettyRequestUpgradeStrategy() }
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
                    subscription {
                        Subscription(ref())
                    }
                }
                websocket()
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

    @Test
    fun `Test websocket`() {
        val gqlMutate = gqlAddMessage()
        val gqlObserve = gqlObserveMessage()
        val variables = mapOf("message" to mapOf("title" to "1", "content" to "2"))
        with(app.run()) {
            val mapper = getBean<ObjectMapper>()
            val socketClient = ReactorNettyWebSocketClient()
            val httpClient = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:$localServerPort").build()
            val processor = EmitterProcessor.create<String>()
            val response = socketClient.execute(URI.create("ws://127.0.0.1:$localServerPort/graphql")) { session ->
                val input = mapper.writeValueAsString(GraphqlRequest(gqlObserve))
                session.send(Mono.just(session.textMessage(input)))
                    .thenMany(session.receive().map { it.payloadAsText })
                    .take(1)
                    .subscribeWith(processor)
                    .then()
            }
            httpClient.post()
                .uri("/graphql")
                .header("Content-type", "application/json")
                .syncBody(GraphqlRequest(gqlMutate, "addMessage", variables))
                .exchange()
            processor.test().then { response.block() }
                .expectNext("""{"data":{"message":{"title":"1","content":"2"}}}""").thenCancel().verify()
            close()
        }
    }
}