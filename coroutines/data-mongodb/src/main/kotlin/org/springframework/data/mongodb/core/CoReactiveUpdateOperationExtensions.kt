package org.springframework.data.mongodb.core

import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle

/**
 * Coroutines variant of [ReactiveUpdateOperation.TerminatingFindAndModify.findModifyAndAwait].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveUpdateOperation.TerminatingFindAndModify<T>.findModifyAndAwait(): T? = findAndModify().awaitFirstOrNull()


/**
 * Coroutines variant of [ReactiveUpdateOperation.TerminatingFindAndReplace.findAndReplace].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveUpdateOperation.TerminatingFindAndReplace<T>.findReplaceAndAwait(): T? = findAndReplace().awaitFirstOrNull()

/**
 * Coroutines variant of [ReactiveUpdateOperation.TerminatingUpdate.all].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveUpdateOperation.TerminatingUpdate<T>.allAndAwait(): UpdateResult = all().awaitSingle()

/**
 * Coroutines variant of [ReactiveUpdateOperation.TerminatingUpdate.first].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveUpdateOperation.TerminatingUpdate<T>.firstAndAwait(): UpdateResult = first().awaitSingle()

/**
 * Coroutines variant of [ReactiveUpdateOperation.TerminatingUpdate.upsert].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveUpdateOperation.TerminatingUpdate<T>.upsertAndAwait(): UpdateResult = upsert().awaitSingle()

/**
 * Extension for [ReactiveUpdateOperation.FindAndReplaceWithProjection.as] leveraging reified type parameters.
 *
 * @author Sebastien Deleuze
 */
inline fun <reified T : Any> ReactiveUpdateOperation.FindAndReplaceWithProjection<T>.asType(): ReactiveUpdateOperation.FindAndReplaceWithOptions<T> = `as`(T::class.java)







