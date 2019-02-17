package org.springframework.data.mongodb.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.geo.Distance
import org.springframework.data.geo.GeoResult
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CoReactiveFindOperationExtensionsTests {

    @Test
    fun terminatingFindAwaitOne() {
        val find = mockk<ReactiveFindOperation.TerminatingFind<String>>()
        every { find.one() } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", find.awaitOne())
        }
        verify {
            find.one()
        }
    }

    @Test
    fun terminatingFindAwaitFirst() {
        val find = mockk<ReactiveFindOperation.TerminatingFind<String>>()
        every { find.first() } returns Mono.just("foo")
        runBlocking {
            assertEquals("foo", find.awaitFirst())
        }
        verify {
            find.first()
        }
    }

    @Test
    fun terminatingFindAwaitAll() {
        val find = mockk<ReactiveFindOperation.TerminatingFind<String>>()
        every { find.all() } returns Flux.just("foo", "bar")
        runBlocking {
            assertEquals(listOf("foo", "bar"), find.awaitAll())
        }
        verify {
            find.all()
        }
    }

    @Test
    fun terminatingFindTail() {
        val find = mockk<ReactiveFindOperation.TerminatingFind<String>>()
        every { find.tail() } returns Flux.just("foo", "bar")
        runBlocking {
            val channel: ReceiveChannel<String> = find.tailToChannel()
            assertEquals("foo", channel.receive())
            assertEquals("bar", channel.receive())
            channel.cancel()
        }
        verify {
            find.tail()
        }
    }

    @Test
    fun terminatingFindAwaitCount() {
        val find = mockk<ReactiveFindOperation.TerminatingFind<String>>()
        every { find.count() } returns Mono.just(1)
        runBlocking {
            assertEquals(1, find.awaitCount())
        }
        verify {
            find.count()
        }
    }

    @Test
    fun terminatingFindAwaitExists() {
        val find = mockk<ReactiveFindOperation.TerminatingFind<String>>()
        every { find.exists() } returns Mono.just(true)
        runBlocking {
            assertEquals(true, find.awaitExists())
        }
        verify {
            find.exists()
        }
    }

    @Test
    fun terminatingFindNearAwaitAll() {
        val find = mockk<ReactiveFindOperation.TerminatingFindNear<String>>()
        val resultFoo = GeoResult("foo", Distance(0.0))
        val resultBar = GeoResult("bar", Distance(0.0))
        every { find.all() } returns Flux.just(resultFoo, resultBar)
        runBlocking {
            assertEquals(listOf(resultFoo, resultBar), find.awaitAll())
        }
        verify {
            find.all()
        }
    }

    @Test
    fun terminatingDistinctAwaitAll() {
        val find = mockk<ReactiveFindOperation.TerminatingDistinct<String>>()
        every { find.all() } returns Flux.just("foo", "bar")
        runBlocking {
            assertEquals(listOf("foo", "bar"), find.awaitAll())
        }
        verify {
            find.all()
        }
    }


}