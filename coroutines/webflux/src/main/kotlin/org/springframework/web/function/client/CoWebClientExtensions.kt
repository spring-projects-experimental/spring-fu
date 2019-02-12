package org.springframework.web.function.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * Coroutines variant of [WebClient.RequestHeadersSpec.exchange].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun WebClient.RequestHeadersSpec<out WebClient.RequestHeadersSpec<*>>.exchangeAndAwait(): ClientResponse = exchange().awaitSingle()

/**
 * Coroutines variant of [WebClient.RequestBodySpec.body].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
inline fun <reified T: Any> WebClient.RequestBodySpec.body(crossinline supplier: suspend () -> T)
        = body(GlobalScope.mono(Dispatchers.Unconfined) { supplier.invoke() })

/**
 * Coroutines variant of [WebClient.ResponseSpec.bodyToMono].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend inline fun <reified T : Any> WebClient.ResponseSpec.awaitBody() : T? = bodyToMono<T>().awaitFirstOrNull()