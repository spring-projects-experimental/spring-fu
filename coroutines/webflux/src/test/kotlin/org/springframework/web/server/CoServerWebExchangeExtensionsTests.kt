package org.springframework.web.server

import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Mono
import java.security.Principal

class CoServerWebExchangeExtensionsTests {

    @Test
    fun awaitFormData() {
        val exchange = mockk<ServerWebExchange>()
        val map = mockk<MultiValueMap<String, String>>()
        every { exchange.formData } returns Mono.just(map)
        runBlocking {
            Assertions.assertEquals(map, exchange.awaitFormData())
        }
    }

    @Test
    fun awaitMultipartData() {
        val exchange = mockk<ServerWebExchange>()
        val map = mockk<MultiValueMap<String, Part>>()
        every { exchange.multipartData } returns Mono.just(map)
        runBlocking {
            Assertions.assertEquals(map, exchange.awaitMultipartData())
        }
    }

    @Test
    fun awaitPrincipal() {
        val exchange = mockk<ServerWebExchange>()
        val principal = mockk<Principal>()
        every { exchange.getPrincipal<Principal>() } returns Mono.just(principal)
        runBlocking {
            Assertions.assertEquals(principal, exchange.awaitPrincipal())
        }
    }

    @Test
    fun awaitSession() {
        val exchange = mockk<ServerWebExchange>()
        val session = mockk<WebSession>()
        every { exchange.session } returns Mono.just(session)
        runBlocking {
            Assertions.assertEquals(session, exchange.awaitSession())
        }
    }

    @Test
    fun `Builder with principal using a suspendable function parameter`() {
        val builder = mockk<ServerWebExchange.Builder>()
        val supplier: suspend () -> Principal = mockk()
        every { builder.principal(ofType<Mono<Principal>>()) } returns builder
        runBlocking {
            builder.principal(supplier)
        }
        verify {
            builder.principal(ofType<Mono<Principal>>())
        }
    }

}