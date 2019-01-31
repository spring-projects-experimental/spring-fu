package org.springframework.web.function.client

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.client.toEntity
import org.springframework.web.reactive.function.client.toEntityList

/**
 * Coroutines variant of [ClientResponse.bodyToMono].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend inline fun <reified T : Any> ClientResponse.awaitBody(): T? = bodyToMono<T>().awaitFirstOrNull()

/**
 * Coroutines variant of [ClientResponse.toEntity].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend inline fun <reified T : Any> ClientResponse.awaitEntity(): ResponseEntity<T> = toEntity<T>().awaitSingle()

/**
 * Coroutines variant of [ClientResponse.toEntityList].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend inline fun <reified T : Any> ClientResponse.awaitEntityList(): ResponseEntity<List<T>> = toEntityList<T>().awaitSingle()
