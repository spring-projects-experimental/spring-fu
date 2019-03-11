package org.springframework.data.mongodb.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class CoReactiveRemoveOperationExtensionsTests {

    @Test
    fun findRemoveAndAwait() {
        val remove = mockk<ReactiveRemoveOperation.TerminatingRemove<String>>()
        every { remove.findAndRemove() } returns Flux.just("foo", "bar")
        runBlocking {
            assertEquals(listOf("foo", "bar"), remove.findRemoveAndAwait())
        }
        verify {
            remove.findAndRemove()
        }
    }

}