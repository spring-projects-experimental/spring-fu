package org.springframework.data.r2dbc.function

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

class DatabaseClientExtensionsTests {

    @Test
    fun genericExecuteSpecAwait() {
        val spec = mockk<DatabaseClient.GenericExecuteSpec>()
        every { spec.then() } returns Mono.empty()
        runBlocking {
            spec.await()
        }
        verify {
            spec.then()
        }
    }

    @Test
    fun genericExecuteSpecAsType() {
        val genericSpec = mockk<DatabaseClient.GenericExecuteSpec>()
        val typedSpec: DatabaseClient.TypedExecuteSpec<String> = mockk()
        every { genericSpec.`as`(String::class.java) } returns typedSpec
        runBlocking {
            assertEquals(typedSpec, genericSpec.asType<String>())
        }
        verify {
            genericSpec.`as`(String::class.java)
        }
    }

    @Test
    fun genericSelectSpecAsType() {
        val genericSpec = mockk<DatabaseClient.GenericSelectSpec>()
        val typedSpec: DatabaseClient.TypedSelectSpec<String> = mockk()
        every { genericSpec.`as`(String::class.java) } returns typedSpec
        runBlocking {
            assertEquals(typedSpec, genericSpec.asType<String>())
        }
        verify {
            genericSpec.`as`(String::class.java)
        }
    }

    @Test
    fun typedExecuteSpecAwait() {
        val spec = mockk<DatabaseClient.TypedExecuteSpec<String>>()
        every { spec.then() } returns Mono.empty()
        runBlocking {
            spec.await()
        }
        verify {
            spec.then()
        }
    }

    @Test
    fun typedExecuteSpecAsType() {
        val spec: DatabaseClient.TypedExecuteSpec<String> = mockk()
        every { spec.`as`(String::class.java) } returns spec
        runBlocking {
            assertEquals(spec, spec.asType())
        }
        verify {
            spec.`as`(String::class.java)
        }
    }

    @Test
    fun insertSpecAwait() {
        val spec = mockk<DatabaseClient.InsertSpec<String>>()
        every { spec.then() } returns Mono.empty()
        runBlocking {
            spec.await()
        }
        verify {
            spec.then()
        }
    }

    @Test
    fun insertIntoSpecInto() {
        val spec = mockk<DatabaseClient.InsertIntoSpec>()
        val typedSpec: DatabaseClient.TypedInsertSpec<String> = mockk()
        every { spec.into(String::class.java) } returns typedSpec
        runBlocking {
            assertEquals(typedSpec, spec.into<String>())
        }
        verify {
            spec.into(String::class.java)
        }
    }

}
