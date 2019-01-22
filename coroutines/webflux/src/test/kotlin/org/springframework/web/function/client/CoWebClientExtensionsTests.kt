package org.springframework.web.function.client

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.web.function.server.awaitBody
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

class CoWebClientExtensionsTests {

    @Test
    fun awaitExchange() {
        val spec = mockk<WebClient.RequestHeadersSpec<*>>()
        val response = mockk<ClientResponse>()
        every { spec.exchange() } returns Mono.just(response)
        runBlocking {
            Assertions.assertEquals(response, spec.awaitExchange())
        }
    }

    @Test
    fun body() {
        val bodySpec = mockk<WebClient.RequestBodySpec>()
        val headerSpec = mockk<WebClient.RequestHeadersSpec<*>>()
        val supplier: suspend () -> String = mockk()
        every { bodySpec.body(ofType<Mono<String>>()) } returns headerSpec
        runBlocking {
            bodySpec.body(supplier)
        }
        verify {
            bodySpec.body(ofType<Mono<String>>())
        }
    }

    @Test
    fun awaitBody() {
        val spec = mockk<WebClient.ResponseSpec>()
        every { spec.bodyToMono<String>() } returns Mono.just("foo")
        runBlocking {
            Assertions.assertEquals("foo", spec.awaitBody<String>())
        }
    }
}