package org.springframework.data.mongodb.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class CoReactiveMapReduceOperationExtensionsTests {

    @Test
    fun terminatingFindAwaitAll() {
        val find = mockk<ReactiveMapReduceOperation.TerminatingMapReduce<String>>()
        every { find.all() } returns Flux.just("foo", "bar")
        runBlocking {
            Assertions.assertEquals(listOf("foo", "bar"), find.awaitAll())
        }
        verify {
            find.all()
        }
    }

}