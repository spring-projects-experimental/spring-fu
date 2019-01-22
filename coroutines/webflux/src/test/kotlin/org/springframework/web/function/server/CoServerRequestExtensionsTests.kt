package org.springframework.web.function.server

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono
import java.security.Principal

class CoServerRequestExtensionsTests {

    @Test
    fun awaitBody() {
        val request = mockk<ServerRequest>()
        every { request.bodyToMono<String>() } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", request.awaitBody<String>())
        }
    }

    @Test
    fun awaitBodyNull() {
        val request = mockk<ServerRequest>()
        every { request.bodyToMono<String>() } returns Mono.empty()
        runBlocking {
            assertNull(request.awaitBody<String>())
        }
    }

    @Test
    fun awaitFormData() {
        val request = mockk<ServerRequest>()
        val map = mockk<MultiValueMap<String, String>>()
        every { request.formData() } returns Mono.just(map)
        runBlocking {
            assertEquals(map, request.awaitFormData())
        }
    }

    @Test
    fun awaitMultipartData() {
        val request = mockk<ServerRequest>()
        val map = mockk<MultiValueMap<String, Part>>()
        every { request.multipartData() } returns Mono.just(map)
        runBlocking {
            assertEquals(map, request.awaitMultipartData())
        }
    }

    @Test
    fun awaitPrincipal() {
        val request = mockk<ServerRequest>()
        val principal = mockk<Principal>()
        every { request.principal() } returns Mono.just(principal)
        runBlocking {
            assertEquals(principal, request.awaitPrincipal())
        }
    }

    @Test
    fun awaitSession() {
        val request = mockk<ServerRequest>()
        val session = mockk<WebSession>()
        every { request.session() } returns Mono.just(session)
        runBlocking {
            assertEquals(session, request.awaitSession())
        }
    }

}