package org.springframework.data.mongodb.core

import kotlinx.coroutines.reactive.awaitSingle

/**
 * Coroutines variant of [ReactiveRemoveOperation.TerminatingRemove.all].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T : Any> ReactiveRemoveOperation.TerminatingRemove<T>.findRemoveAndAwait(): List<T> = findAndRemove().collectList().awaitSingle()