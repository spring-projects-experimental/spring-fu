package org.springframework.data.mongodb.core

import com.mongodb.client.result.DeleteResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CoReactiveRemoveOperationExtensionsTests {

    @Test
    fun allAndAwait() {
        val remove = mockk<ReactiveRemoveOperation.TerminatingRemove<String>>()
        val result = mockk<DeleteResult>()
        every { remove.all() } returns Mono.just(result)
        runBlocking {
            assertEquals(result, remove.allAndAwait())
        }
        verify {
            remove.all()
        }
    }

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