package org.springframework.data.r2dbc.function

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class RowsFetchSpecExtensionsTests {

    @Test
    fun awaitOne() {
        val spec = mockk<RowsFetchSpec<String>>()
        every { spec.one() } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", spec.awaitOne())
        }
        verify {
            spec.one()
        }
    }

    @Test
    fun awaitFirst() {
        val spec = mockk<RowsFetchSpec<String>>()
        every { spec.first() } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", spec.awaitFirst())
        }
        verify {
            spec.first()
        }
    }

    @Test
    fun awaitAll() {
        val spec = mockk<RowsFetchSpec<String>>()
        every { spec.all() } returns Flux.just("foo", "bar")
        runBlocking {
            assertEquals(listOf("foo", "bar"), spec.awaitAll())
        }
        verify {
            spec.all()
        }
    }
}
