package org.springframework.data.r2dbc.function

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class RowsFetchSpecExtensionsTests {

    @Test
    fun awaitOne() {
        val spec = mockk<RowsFetchSpec<String>>()
        every { spec.one() } returns Mono.just("foo")
        runBlocking {
            Assertions.assertEquals("foo", spec.awaitOne())
        }
    }

    @Test
    fun awaitFirst() {
        val spec = mockk<RowsFetchSpec<String>>()
        every { spec.one() } returns Mono.just("foo")
        runBlocking {
            Assertions.assertEquals("foo", spec.awaitOne())
        }
    }

    @Test
    fun awaitAll() {
        val spec = mockk<RowsFetchSpec<String>>()
        every { spec.all() } returns Flux.just("foo", "bar")
        runBlocking {
            Assertions.assertEquals(listOf("foo", "bar"), spec.awaitAll())
        }
    }
}
