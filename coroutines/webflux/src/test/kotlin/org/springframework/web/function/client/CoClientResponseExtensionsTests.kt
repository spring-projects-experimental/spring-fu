package org.springframework.web.function.client

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.client.toEntity
import org.springframework.web.reactive.function.client.toEntityList
import reactor.core.publisher.Mono

class CoClientResponseExtensionsTests {

    @Test
    fun awaitBody() {
        val response = mockk<ClientResponse>()
        every { response.bodyToMono<String>() } returns Mono.just("foo")
        runBlocking {
            Assertions.assertEquals("foo", response.awaitBody<String>())
        }
    }

    @Test
    fun awaitEntity() {
        val response = mockk<ClientResponse>()
        val entity = ResponseEntity("foo", HttpStatus.OK)
        every { response.toEntity<String>() } returns Mono.just(entity)
        runBlocking {
            Assertions.assertEquals(entity, response.awaitEntity<String>())
        }
    }

    @Test
    fun awaitEntityList() {
        val response = mockk<ClientResponse>()
        val entity = ResponseEntity(listOf("foo"), HttpStatus.OK)
        every { response.toEntityList<String>() } returns Mono.just(entity)
        runBlocking {
            Assertions.assertEquals(entity, response.awaitEntityList<String>())
        }
    }
}