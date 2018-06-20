/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.module.webflux

import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.SmartLifecycle
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.context.support.registerBean
import org.springframework.core.codec.*
import org.springframework.core.io.Resource
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.fu.ApplicationDsl
import org.springframework.fu.AbstractModule
import org.springframework.fu.Module
import org.springframework.fu.module.webflux.server.KotlinHandlerFunction
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler
import org.springframework.web.server.WebFilter
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver
import reactor.core.publisher.Mono

/**
 * @author Sebastien Deleuze
 */
open class WebFluxModule(private val init: WebFluxModule.() -> Unit): AbstractModule() {

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		init()
		super.initialize(context)
	}

	fun server(server: WebServerModule, init: WebFluxServerModule.() -> Unit =  {}) {
		initializers.add(WebFluxServerModule(init, server))
	}

	fun client(baseUrl: String? = null, name: String? = null, init: WebFluxClientModule.() -> Unit =  {}) {
		initializers.add(WebFluxClientModule(init, baseUrl, name))
	}

	open class WebFluxServerModule(private val init: WebFluxServerModule.() -> Unit,
							  private val serverModule: WebServerModule): AbstractModule() {

		private val builder = HandlerStrategies.empty()

		override fun initialize(context: GenericApplicationContext) {
			this.context = context
			init()
			initializers.add(serverModule)
			initializers.add(beans {
				bean("webHandler") {
					builder.exceptionHandler(WebFluxResponseStatusExceptionHandler())
					builder.localeContextResolver(AcceptHeaderLocaleContextResolver())
					builder.codecs {
						with(it.customCodecs()) {
							encoder(CharSequenceEncoder.textPlainOnly())
							decoder(org.springframework.core.codec.ResourceDecoder())
							decoder(org.springframework.core.codec.StringDecoder.textPlainOnly())
						}
					}
					for (c in initializers) {
						if (c is WebFluxCodecsModule) {
							for (codec in c.initializers) {
								if (codec is WebFluxServerCodecModule) {
									builder.codecs({ codec.invoke(it) })
								}
							}
						}
					}

					try {
						builder.viewResolver(ref())
					}
					catch (ex: NoSuchBeanDefinitionException) {}
					val routers = context.getBeansOfType<RouterFunction<ServerResponse>>()
					val router = if (!routers.isEmpty()) {
						routers.values.reduce(RouterFunction<ServerResponse>::and)
					}
					else {
						RouterFunction<ServerResponse> { Mono.empty() }
					}

					RouterFunctions.toWebHandler(router, builder.build())
				}
			})
			super.initialize(context)
		}

		fun codecs(init: WebFluxCodecsModule.() -> Unit =  {}) {
			initializers.add(WebFluxCodecsModule(init))
		}

		fun filter(filter: WebFilter) {
			builder.webFilter(filter)
		}

		fun routes(ref: (() -> WebFluxRoutesModule)? = null, routes: (WebFluxRoutesModule.() -> Unit)? = null) {
			if (routes == null && ref == null)
				throw IllegalArgumentException("No routes provided")

			routes?.let { initializers.add(WebFluxRoutesModule(it)) }
			ref?.let { initializers.add(it.invoke()) }
		}


	}

	class WebFluxClientModule(private val init: WebFluxClientModule.() -> Unit, val baseUrl: String?, val name: String?) : AbstractModule() {

		private val builder = WebClient.builder()

		override fun initialize(context: GenericApplicationContext) {
			init()
			super.initialize(context)
		}

		init {
			initializers.add(beans {
				bean(name = name) {
					if (baseUrl != null) {
						builder.baseUrl(baseUrl)
					}
					val exchangeStrategiesBuilder = ExchangeStrategies.builder()
					exchangeStrategiesBuilder.codecs {
						with(it.customCodecs()) {
							encoder(CharSequenceEncoder.textPlainOnly())
							decoder(ResourceDecoder())
							decoder(StringDecoder.textPlainOnly())
						}
					}
					for (c in initializers) {
						if (c is WebFluxCodecsModule) {
							for (codec in c.initializers) {
								if (codec is WebFluxClientCodecModule) {
									exchangeStrategiesBuilder.codecs({ codec.invoke(it) })
								}
							}
						}
					}
					builder.exchangeStrategies(exchangeStrategiesBuilder.build())
					builder.build()
				}
			})
		}

		fun codecs(init: WebFluxCodecsModule.() -> Unit =  {}) {
			initializers.add(WebFluxCodecsModule(init))
		}
	}

	class WebFluxCodecsModule(private val init: WebFluxCodecsModule.() -> Unit): AbstractModule() {
		override fun initialize(context: GenericApplicationContext) {
			init()
			super.initialize(context)
		}
	}

	interface WebServerModule: Module {
		val baseUrl: String
	}

	abstract class AbstractWebServerModule(port: Int, host: String = "0.0.0.0"): AbstractModule(), WebServerModule {
		override val baseUrl = "http://$host:$port"
	}

	interface WebFluxServerCodecModule: Module, (ServerCodecConfigurer) -> (Unit)

	interface WebFluxClientCodecModule: Module, (ClientCodecConfigurer) -> (Unit)

}

abstract class WebServer(private val port: Int) : SmartLifecycle, ApplicationContextAware {

	lateinit var context: ApplicationContext

	override fun isAutoStartup() = true

	override fun getPhase() = Integer.MIN_VALUE

	override fun setApplicationContext(context: ApplicationContext) {
		this.context = context
	}
}

fun ApplicationDsl.webflux(init: WebFluxModule.() -> Unit): WebFluxModule {
	val webFluxDsl = WebFluxModule(init)
	initializers.add(webFluxDsl)
	return webFluxDsl
}

open class WebFluxRoutesModule(private val init: WebFluxRoutesModule.() -> Unit) : AbstractModule(), () -> RouterFunction<ServerResponse> {

	companion object {
		var count = 0
	}

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		val name = "${RouterFunction::class.qualifiedName}${count++}"
		println(name)
		context.registerBean(name) {
			init()
			invoke()
		}
	}

	val routes = mutableListOf<RouterFunction<ServerResponse>>()

	/**
	 * Return a composed request predicate that tests against both this predicate AND
	 * the [other] predicate (String processed as a path predicate). When evaluating the
	 * composed predicate, if this predicate is `false`, then the [other] predicate is not
	 * evaluated.
	 * @see RequestPredicate.and
	 * @see RequestPredicates.path
	 */
	infix fun RequestPredicate.and(other: String): RequestPredicate = this.and(path(other))

	/**
	 * Return a composed request predicate that tests against both this predicate OR
	 * the [other] predicate (String processed as a path predicate). When evaluating the
	 * composed predicate, if this predicate is `true`, then the [other] predicate is not
	 * evaluated.
	 * @see RequestPredicate.or
	 * @see RequestPredicates.path
	 */
	infix fun RequestPredicate.or(other: String): RequestPredicate = this.or(path(other))

	/**
	 * Return a composed request predicate that tests against both this predicate (String
	 * processed as a path predicate) AND the [other] predicate. When evaluating the
	 * composed predicate, if this predicate is `false`, then the [other] predicate is not
	 * evaluated.
	 * @see RequestPredicate.and
	 * @see RequestPredicates.path
	 */
	infix fun String.and(other: RequestPredicate): RequestPredicate = path(this).and(other)

	/**
	 * Return a composed request predicate that tests against both this predicate (String
	 * processed as a path predicate) OR the [other] predicate. When evaluating the
	 * composed predicate, if this predicate is `true`, then the [other] predicate is not
	 * evaluated.
	 * @see RequestPredicate.or
	 * @see RequestPredicates.path
	 */
	infix fun String.or(other: RequestPredicate): RequestPredicate = path(this).or(other)

	/**
	 * Return a composed request predicate that tests against both this predicate AND
	 * the [other] predicate. When evaluating the composed predicate, if this
	 * predicate is `false`, then the [other] predicate is not evaluated.
	 * @see RequestPredicate.and
	 */
	infix fun RequestPredicate.and(other: RequestPredicate): RequestPredicate = this.and(other)

	/**
	 * Return a composed request predicate that tests against both this predicate OR
	 * the [other] predicate. When evaluating the composed predicate, if this
	 * predicate is `true`, then the [other] predicate is not evaluated.
	 * @see RequestPredicate.or
	 */
	infix fun RequestPredicate.or(other: RequestPredicate): RequestPredicate = this.or(other)

	/**
	 * Return a predicate that represents the logical negation of this predicate.
	 */
	operator fun RequestPredicate.not(): RequestPredicate = this.negate()

	/**
	 * Route to the given router function if the given request predicate applies. This
	 * method can be used to create *nested routes*, where a group of routes share a
	 * common path (prefix), header, or other request predicate.
	 * @see RouterFunctions.nest
	 */
	fun RequestPredicate.nest(init: RouterFunctionDsl.() -> Unit) {
		routes += RouterFunctions.nest(this, RouterFunctionDsl(init).invoke())
	}

	/**
	 * Route to the given router function if the given request predicate (String
	 * processed as a path predicate) applies. This method can be used to create
	 * *nested routes*, where a group of routes share a common path
	 * (prefix), header, or other request predicate.
	 * @see RouterFunctions.nest
	 * @see RequestPredicates.path
	 */
	fun String.nest(init: RouterFunctionDsl.() -> Unit) {
		routes += RouterFunctions.nest(path(this), RouterFunctionDsl(init).invoke())
	}

	/**
	 * Route to the given handler function if the given request predicate applies.
	 * @see RouterFunctions.route
	 */
	fun GET(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.GET(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.GET(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code GET}
	 * and the given {@code pattern} matches against the request path.
	 * @see RequestPredicates.GET
	 */
	fun GET(pattern: String): RequestPredicate = RequestPredicates.GET(pattern)

	/**
	 * Route to the given handler function if the given request predicate applies.
	 * @see RouterFunctions.route
	 */
	fun HEAD(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.HEAD(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.HEAD(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code HEAD}
	 * and the given {@code pattern} matches against the request path.
	 * @see RequestPredicates.HEAD
	 */
	fun HEAD(pattern: String): RequestPredicate = RequestPredicates.HEAD(pattern)

	/**
	 * Route to the given handler function if the given POST predicate applies.
	 * @see RouterFunctions.route
	 */
	fun POST(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.POST(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.POST(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code POST}
	 * and the given {@code pattern} matches against the request path.
	 * @see RequestPredicates.POST
	 */
	fun POST(pattern: String): RequestPredicate = RequestPredicates.POST(pattern)

	/**
	 * Route to the given handler function if the given PUT predicate applies.
	 * @see RouterFunctions.route
	 */
	fun PUT(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.PUT(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.PUT(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code PUT}
	 * and the given {@code pattern} matches against the request path.
	 * @see RequestPredicates.PUT
	 */
	fun PUT(pattern: String): RequestPredicate = RequestPredicates.PUT(pattern)

	/**
	 * Route to the given handler function if the given PATCH predicate applies.
	 * @see RouterFunctions.route
	 */
	fun PATCH(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.PATCH(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.PATCH(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code PATCH}
	 * and the given {@code pattern} matches against the request path.
	 * @param pattern the path pattern to match against
	 * @return a predicate that matches if the request method is PATCH and if the given pattern
	 * matches against the request path
	 */
	fun PATCH(pattern: String): RequestPredicate = RequestPredicates.PATCH(pattern)

	/**
	 * Route to the given handler function if the given DELETE predicate applies.
	 * @see RouterFunctions.route
	 */
	fun DELETE(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.DELETE(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.DELETE(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code DELETE}
	 * and the given {@code pattern} matches against the request path.
	 * @param pattern the path pattern to match against
	 * @return a predicate that matches if the request method is DELETE and if the given pattern
	 * matches against the request path
	 */
	fun DELETE(pattern: String): RequestPredicate = RequestPredicates.DELETE(pattern)

	/**
	 * Route to the given handler function if the given OPTIONS predicate applies.
	 * @see RouterFunctions.route
	 */
	fun OPTIONS(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.OPTIONS(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.OPTIONS(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code OPTIONS}
	 * and the given {@code pattern} matches against the request path.
	 * @param pattern the path pattern to match against
	 * @return a predicate that matches if the request method is OPTIONS and if the given pattern
	 * matches against the request path
	 */
	fun OPTIONS(pattern: String): RequestPredicate = RequestPredicates.OPTIONS(pattern)

	/**
	 * Route to the given handler function if the given accept predicate applies.
	 * @see RouterFunctions.route
	 */
	fun accept(mediaType: MediaType, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.accept(mediaType), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.accept(mediaType), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that tests if the request's
	 * {@linkplain ServerRequest.Headers#accept() accept} header is
	 * {@linkplain MediaType#isCompatibleWith(MediaType) compatible} with any of the given media types.
	 * @param mediaTypes the media types to match the request's accept header against
	 * @return a predicate that tests the request's accept header against the given media types
	 */
	fun accept(mediaType: MediaType): RequestPredicate = RequestPredicates.accept(mediaType)

	/**
	 * Route to the given handler function if the given contentType predicate applies.
	 * @see RouterFunctions.route
	 */
	fun contentType(mediaType: MediaType, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.contentType(mediaType), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.contentType(mediaType), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that tests if the request's
	 * {@linkplain ServerRequest.Headers#contentType() content type} is
	 * {@linkplain MediaType#includes(MediaType) included} by any of the given media types.
	 * @param mediaTypes the media types to match the request's content type against
	 * @return a predicate that tests the request's content type against the given media types
	 */
	fun contentType(mediaType: MediaType): RequestPredicate = RequestPredicates.contentType(mediaType)

	/**
	 * Route to the given handler function if the given headers predicate applies.
	 * @see RouterFunctions.route
	 */
	fun headers(headersPredicate: (ServerRequest.Headers) -> Boolean, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.headers(headersPredicate), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.headers(headersPredicate), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that tests the request's headers against the given headers predicate.
	 * @param headersPredicate a predicate that tests against the request headers
	 * @return a predicate that tests against the given header predicate
	 */
	fun headers(headersPredicate: (ServerRequest.Headers) -> Boolean): RequestPredicate =
			RequestPredicates.headers(headersPredicate)

	/**
	 * Route to the given handler function if the given method predicate applies.
	 * @see RouterFunctions.route
	 */
	fun method(httpMethod: HttpMethod, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.method(httpMethod), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.method(httpMethod), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that tests against the given HTTP method.
	 * @param httpMethod the HTTP method to match to
	 * @return a predicate that tests against the given HTTP method
	 */
	fun method(httpMethod: HttpMethod): RequestPredicate = RequestPredicates.method(httpMethod)

	/**
	 * Route to the given handler function if the given path predicate applies.
	 * @see RouterFunctions.route
	 */
	fun path(pattern: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.path(pattern), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.path(pattern), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that tests the request path against the given path pattern.
	 * @see RequestPredicates.path
	 */
	fun path(pattern: String): RequestPredicate = RequestPredicates.path(pattern)

	/**
	 * Route to the given handler function if the given pathExtension predicate applies.
	 * @see RouterFunctions.route
	 */
	fun pathExtension(extension: String, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.pathExtension(extension), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.pathExtension(extension), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if the request's path has the given extension.
	 * @param extension the path extension to match against, ignoring case
	 * @return a predicate that matches if the request's path has the given file extension
	 */
	fun pathExtension(extension: String): RequestPredicate = RequestPredicates.pathExtension(extension)

	/**
	 * Route to the given handler function if the given pathExtension predicate applies.
	 * @see RouterFunctions.route
	 */
	fun pathExtension(predicate: (String) -> Boolean, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.pathExtension(predicate), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.pathExtension(predicate), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that matches if the request's path matches the given
	 * predicate.
	 * @see RequestPredicates.pathExtension
	 */
	fun pathExtension(predicate: (String) -> Boolean): RequestPredicate =
			RequestPredicates.pathExtension(predicate)

	/**
	 * Route to the given handler function if the given queryParam predicate applies.
	 * @see RouterFunctions.route
	 */
	fun queryParam(name: String, predicate: (String) -> Boolean, ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.queryParam(name, predicate), HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.queryParam(name, predicate), HandlerFunction { ref(it) }) }
	}

	/**
	 * Return a {@code RequestPredicate} that tests the request's query parameter of the given name
	 * against the given predicate.
	 * @param name the name of the query parameter to test against
	 * @param predicate predicate to test against the query parameter value
	 * @return a predicate that matches the given predicate against the query parameter of the given name
	 * @see ServerRequest#queryParam(String)
	 */
	fun queryParam(name: String, predicate: (String) -> Boolean): RequestPredicate =
			RequestPredicates.queryParam(name, predicate)

	/**
	 * Route to the given handler function if the given request predicate applies.
	 * @see RouterFunctions.route
	 */
	operator fun RequestPredicate.invoke(ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(this, HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(this, HandlerFunction { ref(it) }) }
	}

	/**
	 * Route to the given handler function if the given predicate (String
	 * processed as a path predicate) applies.
	 * @see RouterFunctions.route
	 */
	operator fun String.invoke(ref: ((ServerRequest) -> Mono<ServerResponse>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<ServerResponse>)? = null) {
		f?.let { routes += RouterFunctions.route(RequestPredicates.path(this),  HandlerFunction { KotlinHandlerFunction().f(it) }) }
		ref?.let { routes += RouterFunctions.route(RequestPredicates.path(this),  HandlerFunction { ref(it) }) }
	}

	/**
	 * Route requests that match the given pattern to resources relative to the given root location.
	 * @see RouterFunctions.resources
	 */
	fun resources(path: String, location: Resource) {
		routes += RouterFunctions.resources(path, location)
	}

	/**
	 * Route to resources using the provided lookup function. If the lookup function provides a
	 * [Resource] for the given request, it will be it will be exposed using a
	 * [HandlerFunction] that handles GET, HEAD, and OPTIONS requests.
	 */
	fun resources(ref: ((ServerRequest) -> Mono<Resource>)? = null, f: (KotlinHandlerFunction.(ServerRequest) -> Mono<Resource>)? = null) {
		f?.let { routes += RouterFunctions.resources { KotlinHandlerFunction().f(it) } }
		ref?.let { routes += RouterFunctions.resources { ref(it) } }
	}


	/**
	 * Return a composed routing function created from all the registered routes.
	 */
	override fun invoke(): RouterFunction<ServerResponse> {
		init()
		return routes.reduce(RouterFunction<ServerResponse>::and)
	}
}

fun routes(routes: WebFluxRoutesModule.() -> Unit) = WebFluxRoutesModule(routes)
