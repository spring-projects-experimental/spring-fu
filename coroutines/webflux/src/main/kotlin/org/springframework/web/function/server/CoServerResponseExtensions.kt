package org.springframework.web.function.server

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body


/**
 * Coroutines variant of [ServerResponse.HeadersBuilder.build].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerResponse.HeadersBuilder<out ServerResponse.HeadersBuilder<*>>.await(): ServerResponse = build().awaitSingle()

/**
 * Coroutines variant of [ServerResponse.BodyBuilder.body].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend inline fun <reified T : Any> ServerResponse.BodyBuilder.bodyAndAwait(crossinline supplier: suspend () -> T): ServerResponse =
        coroutineScope { body<T>(mono { supplier.invoke() }) }.awaitSingle()

/**
 * Coroutines variant of [ServerResponse.BodyBuilder.syncBody].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerResponse.BodyBuilder.bodyAndAwait(body: Any): ServerResponse =
        syncBody(body).awaitSingle()


/**
 * Coroutines variant of [ServerResponse.BodyBuilder.syncBody] without the sync prefix since it is ok to use it within
 * another suspendable function.
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerResponse.BodyBuilder.renderAndAwait(name: String, vararg modelAttributes: String): ServerResponse =
        render(name, *modelAttributes).awaitSingle()

/**
 * Coroutines variant of [ServerResponse.BodyBuilder.syncBody] without the sync prefix since it is ok to use it within
 * another suspendable function.
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerResponse.BodyBuilder.renderAndAwait(name: String, model: Map<String, *>): ServerResponse =
        render(name, model).awaitSingle()
