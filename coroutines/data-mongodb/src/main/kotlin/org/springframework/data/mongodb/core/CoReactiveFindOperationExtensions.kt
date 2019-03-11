package org.springframework.data.mongodb.core

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.openSubscription
import org.springframework.data.geo.GeoResult

/**
 * Coroutines variant of [ReactiveFindOperation.TerminatingFind.all].
 *
 * * **Note: This API will become obsolete in future updates with introduction of lazy asynchronous streams.**
 * See [issue #254](https://github.com/Kotlin/kotlinx.coroutines/issues/254).
 *
 * @author Sebastien Deleuze
 */
suspend inline fun <reified T: Any> ReactiveFindOperation.TerminatingFind<T>.awaitAll(): List<T> = all().collectList().awaitSingle()

/**
 * Coroutines variant of [ReactiveFindOperation.TerminatingFind.tail].
 *
 * @author Sebastien Deleuze
 */
@ObsoleteCoroutinesApi
fun <T : Any> ReactiveFindOperation.TerminatingFind<T>.tailToChannel(): ReceiveChannel<T> = tail().openSubscription()

/**
 * Coroutines variant of [ReactiveFindOperation.TerminatingFindNear.all].
 *
 * * **Note: This API will become obsolete in future updates with introduction of lazy asynchronous streams.**
 * See [issue #254](https://github.com/Kotlin/kotlinx.coroutines/issues/254).
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveFindOperation.TerminatingFindNear<T>.awaitAll(): List<GeoResult<T>> = all().collectList().awaitSingle()

/**
 * Coroutines variant of [ReactiveFindOperation.TerminatingDistinct.all].
 *
 * * **Note: This API will become obsolete in future updates with introduction of lazy asynchronous streams.**
 * See [issue #254](https://github.com/Kotlin/kotlinx.coroutines/issues/254).
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveFindOperation.TerminatingDistinct<T>.awaitAll(): List<T> = all().collectList().awaitSingle()
