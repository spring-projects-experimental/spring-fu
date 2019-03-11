package org.springframework.data.mongodb.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class CoReactiveInsertOperationExtensionsTests {

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