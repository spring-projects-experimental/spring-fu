package org.springframework.data.mongodb.core

import com.mongodb.client.result.UpdateResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

class CoReactiveUpdateOperationExtensionsTests {

    @Test
    fun findModifyAndAwait() {
        val find = mockk<ReactiveUpdateOperation.TerminatingFindAndModify<String>>()
        every { find.findAndModify() } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", find.findModifyAndAwait())
        }
        verify {
            find.findAndModify()
        }
    }

    @Test
    fun findReplaceAndAwait() {
        val find = mockk<ReactiveUpdateOperation.TerminatingFindAndReplace<String>>()
        every { find.findAndReplace() } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", find.findReplaceAndAwait())
        }
        verify {
            find.findAndReplace()
        }
    }

    @Test
    fun allAndAwait() {
        val update = mockk<ReactiveUpdateOperation.TerminatingUpdate<String>>()
        val result = mockk<UpdateResult>()
        every { update.all() } returns Mono.just(result)
        runBlocking {
            assertEquals(result, update.allAndAwait())
        }
        verify {
            update.all()
        }
    }

    @Test
    fun firstAndAwait() {
        val update = mockk<ReactiveUpdateOperation.TerminatingUpdate<String>>()
        val result = mockk<UpdateResult>()
        every { update.first() } returns Mono.just(result)
        runBlocking {
            assertEquals(result, update.firstAndAwait())
        }
        verify {
            update.first()
        }
    }

    @Test
    fun upsertAndAwait() {
        val update = mockk<ReactiveUpdateOperation.TerminatingUpdate<String>>()
        val result = mockk<UpdateResult>()
        every { update.upsert() } returns Mono.just(result)
        runBlocking {
            assertEquals(result, update.upsertAndAwait())
        }
        verify {
            update.upsert()
        }
    }

    @Test
    fun findAndReplaceWithProjectionAsType() {
        val update = mockk<ReactiveUpdateOperation.FindAndReplaceWithProjection<String>>()
        val result = mockk<ReactiveUpdateOperation.FindAndReplaceWithOptions<String>>()
        every { update.`as`(String::class.java) } returns result
        assertEquals(result, update.asType<String>())
        verify {
            update.`as`(String::class.java)
        }
    }
}
