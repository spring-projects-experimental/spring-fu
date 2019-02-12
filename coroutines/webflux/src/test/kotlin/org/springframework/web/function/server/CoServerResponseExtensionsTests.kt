package org.springframework.web.function.server

import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class CoServerResponseExtensionsTests {

    @Test
    fun await() {
        val response = mockk<ServerResponse>()
        val builder = mockk<ServerResponse.HeadersBuilder<*>>()
        every { builder.build() } returns Mono.just(response)
        runBlocking {
            assertEquals(response, builder.buildAndAwait())
        }
    }

    @Test
    fun `bodyAndAwait with object parameter`() {
        val response = mockk<ServerResponse>()
        val builder = mockk<ServerResponse.BodyBuilder>()
        val body = "foo"
        every { builder.syncBody(ofType<String>()) } returns Mono.just(response)
        runBlocking {
            builder.bodyAndAwait(body)
        }
        verify {
            builder.syncBody(ofType<String>())
        }
    }

    @Test
    fun `renderAndAwait with a vararg parameter`() {
        val response = mockk<ServerResponse>()
        val builder = mockk<ServerResponse.BodyBuilder>()
        every { builder.render("foo", any(), any()) } returns Mono.just(response)
        runBlocking {
            builder.renderAndAwait("foo", "bar", "baz")
        }
        verify {
            builder.render("foo", any(), any())
        }
    }

    @Test
    fun `renderAndAwait with a Map parameter`() {
        val response = mockk<ServerResponse>()
        val builder = mockk<ServerResponse.BodyBuilder>()
        val map = mockk<Map<String, *>>()
        every { builder.render("foo", map) } returns Mono.just(response)
        runBlocking {
            builder.renderAndAwait("foo", map)
        }
        verify {
            builder.render("foo", map)
        }
    }

}