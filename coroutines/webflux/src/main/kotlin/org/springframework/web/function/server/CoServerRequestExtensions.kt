package org.springframework.web.function.server

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.server.WebSession
import java.security.Principal

/**
 * Coroutines variant of [ServerRequest.bodyToMono].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend inline fun <reified T : Any> ServerRequest.awaitBody(): T? = bodyToMono<T>().awaitFirstOrNull()

/**
 * Coroutines variant of [ServerRequest.formData].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerRequest.awaitFormData(): MultiValueMap<String, String> = formData().awaitSingle()

/**
 * Coroutines variant of [ServerRequest.multipartData].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerRequest.awaitMultipartData(): MultiValueMap<String, Part> = multipartData().awaitSingle()

/**
 * Coroutines variant of [ServerRequest.principal].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerRequest.awaitPrincipal(): Principal = principal().awaitSingle()

/**
 * Coroutines variant of [ServerRequest.session].
 *
 * @author Sebastien Deleuze
 * @since 5.2
 */
suspend fun ServerRequest.awaitSession(): WebSession = session().awaitSingle()
