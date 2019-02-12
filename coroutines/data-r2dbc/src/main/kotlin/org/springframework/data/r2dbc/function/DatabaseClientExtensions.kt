package org.springframework.data.r2dbc.function

import kotlinx.coroutines.reactive.awaitFirstOrNull

/**
 * Coroutines variant of [DatabaseClient.GenericExecuteSpec.then].
 *
 * @author Sebastien Deleuze
 */
suspend fun DatabaseClient.GenericExecuteSpec.await() {
    then().awaitFirstOrNull()
}

/**
 * Extension for [DatabaseClient.GenericExecuteSpec.as] providing a
 * `asType<Foo>()` variant.
 *
 * @author Sebastien Deleuze
 */
inline fun <reified T : Any> DatabaseClient.GenericExecuteSpec.asType() = `as`(T::class.java)

/**
 * Extension for [DatabaseClient.GenericSelectSpec.as] providing a
 * `asType<Foo>()` variant.
 *
 * @author Sebastien Deleuze
 */
inline fun <reified T : Any> DatabaseClient.GenericSelectSpec.asType() = `as`(T::class.java)

/**
 * Coroutines variant of [DatabaseClient.TypedExecuteSpec.then].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T> DatabaseClient.TypedExecuteSpec<T>.await() {
    then().awaitFirstOrNull()
}

/**
 * Extension for [DatabaseClient.TypedExecuteSpec.as] providing a
 * `asType<Foo>()` variant.
 *
 * @author Sebastien Deleuze
 */
inline fun <reified T : Any> DatabaseClient.TypedExecuteSpec<T>.asType() = `as`(T::class.java)

/**
 * Coroutines variant of [DatabaseClient.InsertSpec.then].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T> DatabaseClient.InsertSpec<T>.await() {
    then().awaitFirstOrNull()
}

/**
 * Extension for [DatabaseClient.InsertIntoSpec.into] providing a
 * `into<Foo>()` variant.
 *
 * @author Sebastien Deleuze
 */
inline fun <reified T : Any> DatabaseClient.InsertIntoSpec.into(): DatabaseClient.TypedInsertSpec<T> = into(T::class.java)

