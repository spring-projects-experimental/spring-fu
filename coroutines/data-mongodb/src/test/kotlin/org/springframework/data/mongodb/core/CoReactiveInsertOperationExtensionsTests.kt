package org.springframework.data.mongodb.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CoReactiveInsertOperationExtensionsTests {

    @Test
    fun terminatingFindAwaitOne() {
        val find = mockk<ReactiveInsertOperation.TerminatingInsert<String>>()
        every { find.one("foo") } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", find.oneAndAwait("foo"))
        }
        verify {
            find.one("foo")
        }
    }

    @Test
    fun terminatingFindAwaitAll() {
        val find = mockk<ReactiveInsertOperation.TerminatingInsert<String>>()
        val list = listOf("foo", "bar")
        every { find.all(list) } returns Flux.just("foo", "bar")
        runBlocking {
            assertEquals(list, find.allAndAwait(list))
        }
        verify {
            find.all(list)
        }
    }

}