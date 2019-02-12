package org.springframework.web.function.server

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.reactive.function.server.ServerResponse


/**
 * Coroutines variant of [ServerResponse.HeadersBuilder.build].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerResponse.HeadersBuilder<out ServerResponse.HeadersBuilder<*>>.buildAndAwait(): ServerResponse = build().awaitSingle()

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
