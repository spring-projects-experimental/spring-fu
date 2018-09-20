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

package org.springframework.boot.kofu.web

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.reactive.*
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientInitializer
import org.springframework.boot.kofu.AbstractDsl
import org.springframework.boot.kofu.ApplicationDsl
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.WebFilter

/**
 * Kofu DSL for WebFlux codecs configuration.
 */
abstract class WebFluxCodecDsl : AbstractDsl() {

	override fun register(context: GenericApplicationContext) {
	}

	/**
	 * Enable [org.springframework.core.codec.CharSequenceEncoder] and [org.springframework.core.codec.StringDecoder]
	 */
	abstract fun string()

	/**
	 * Enable [org.springframework.http.codec.ResourceHttpMessageWriter] and [org.springframework.core.codec.ResourceDecoder]
	 */
	abstract fun resource()

	/**
	 * Enable [org.springframework.http.codec.protobuf.ProtobufEncoder] and [org.springframework.http.codec.protobuf.ProtobufDecoder]
	 */
	abstract fun protobuf()
}

class WebFluxClientCodecDsl(private val init: WebFluxClientCodecDsl.() -> Unit) : WebFluxCodecDsl() {

	override fun register(context: GenericApplicationContext) {
		init()
	}

	override fun string() {
		initializers.add(StringCodecInitializer(true))
	}

	override fun resource() {
		initializers.add(ResourceCodecInitializer(true))
	}

	override fun protobuf() {
		initializers.add(ProtobufCodecInitializer(true))
	}
}

class WebFluxServerCodecDsl(private val init: WebFluxServerCodecDsl.() -> Unit) : WebFluxCodecDsl() {

	override fun register(context: GenericApplicationContext) {
		init()
	}

	override fun string() {
		initializers.add(StringCodecInitializer(false))
	}

	override fun resource() {
		initializers.add(ResourceCodecInitializer(false))
	}

	override fun protobuf() {
		initializers.add(ProtobufCodecInitializer(false))
	}
}

/**
 * Kofu DSL for WebFlux server configuration.
 * @author Sebastien Deleuze
 */
open class WebFluxServerDsl(private val init: WebFluxServerDsl.() -> Unit,
							private val serverFactory: ConfigurableReactiveWebServerFactory): AbstractDsl() {

	private val serverProperties = ServerProperties()

	private val resourceProperties = ResourceProperties()

	private val webFluxProperties = WebFluxProperties()

	private var codecsConfigured: Boolean = false

	override fun register(context: GenericApplicationContext) {
		init()
		if (!codecsConfigured) {
			initializers.add(StringCodecInitializer(false))
			initializers.add(ResourceCodecInitializer(false))
		}
		if (context.containsBeanDefinition("webHandler")) {
			throw IllegalStateException("Only one server per application is supported")
		}
		initializers.add(ReactiveWebServerInitializer(serverProperties, resourceProperties, webFluxProperties, serverFactory))
	}

	/**
	 * Configure codecs via a [dedicated DSL][WebFluxServerCodecDsl].
	 * @see org.springframework.boot.kofu.web.jackson
	 * @see WebFluxServerCodecDsl.resource
	 * @see WebFluxServerCodecDsl.string
	 * @see WebFluxServerCodecDsl.protobuf
	 */
	fun codecs(init: WebFluxServerCodecDsl.() -> Unit =  {}) {
		initializers.add(WebFluxServerCodecDsl(init))
		codecsConfigured = true
	}

	/**
	 * Define a request filter for this server
	 */
	fun filter(filter: WebFilter) {
		initializers.add(ApplicationContextInitializer { it.registerBean { filter } })
	}

	/**
	 * Configure codecs via a [dedicated DSL][RouterFunctionDsl].
	 * @sample org.springframework.boot.kofu.samples.routerDsl
	 */
	fun router(routes: (RouterFunctionDsl.() -> Unit)) {
		initializers.add(ApplicationContextInitializer { it.registerBean { RouterFunctionDsl(routes).invoke() } })
	}

	/**
	 * Include and define an external router block for this server
	 * @sample org.springframework.boot.kofu.samples.includeRouter
	 */
	fun include(router: () -> RouterFunction<ServerResponse>) {
		initializers.add(ApplicationContextInitializer { it.registerBean { router.invoke() } })
	}

}

/**
 * Kofu DSL for WebFlux client configuration.
 * @author Sebastien Deleuze
 */
class WebFluxClientDsl(private val init: WebFluxClientDsl.() -> Unit, val baseUrl: String?, private val name: String?) : AbstractDsl() {

	private var codecsConfigured: Boolean = false

	override fun register(context: GenericApplicationContext) {
		init()
		if (!codecsConfigured) {
			initializers.add(StringCodecInitializer(true))
			initializers.add(ResourceCodecInitializer(true))
		}
		ReactiveWebClientInitializer().initialize(context)
		if (name != null)
			context.registerBean(name) { client() }
		else
			context.registerBean { client() }
	}

	private fun client() : WebClient {
		val builder = context.getBean<WebClient.Builder>()
		if (baseUrl != null) {
			builder.baseUrl(baseUrl)
		}
		val exchangeStrategiesBuilder = ExchangeStrategies.builder()
		builder.exchangeStrategies(exchangeStrategiesBuilder.build())
		return builder.build()
	}

	/**
	 * Configure codecs via a [dedicated DSL][WebFluxClientCodecDsl].
	 * @see org.springframework.boot.kofu.web.jackson
	 * @see WebFluxClientCodecDsl.resource
	 * @see WebFluxClientCodecDsl.string
	 * @see WebFluxClientCodecDsl.protobuf
	 */
	fun codecs(init: WebFluxClientCodecDsl.() -> Unit =  {}) {
		initializers.add(WebFluxClientCodecDsl(init))
		codecsConfigured = true
	}
}

/** Use Netty engine in WebFlux server **/
fun ApplicationDsl.netty(port: Int = 8080) = NettyReactiveWebServerFactory(port)

/** Use Tomcat engine in WebFlux server **/
fun ApplicationDsl.tomcat(port: Int = 8080) = TomcatReactiveWebServerFactory(port)

/** Use Undertow engine in WebFlux server **/
fun ApplicationDsl.undertow(port: Int = 8080) = UndertowReactiveWebServerFactory(port)

/** Use Jetty engine in WebFlux server **/
fun ApplicationDsl.jetty(port: Int = 8080) = JettyReactiveWebServerFactory(port)

/**
 * Configure a WebFlux server via a via a [dedicated DSL][WebFluxServerDsl].
 *
 * This DSL configures [WebFlux server](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux).
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 * [ApplicationDsl.startServer] needs to be set to `true` (it is by default).
 *
 * You can chose the underlying engine used:
 *  * Netty (the default)
 *  * Tomcat
 *  * Jetty
 *  * Undertow
 *
 * Require `org.springframework.boot:spring-boot-starter-webflux` dependency.
 *
 * @sample org.springframework.boot.kofu.samples.routerDsl
 * @see WebFluxServerDsl.router
 * @see WebFluxServerDsl.coRouter
 * @see WebFluxServerDsl.codecs
 * @see WebFluxServerDsl.cors
 * @see WebFluxServerDsl.mustache
 * @see org.springframework.boot.kofu.web.netty
 * @see org.springframework.boot.kofu.web.tomcat
 * @see org.springframework.boot.kofu.web.jetty
 * @see org.springframework.boot.kofu.web.undertow
 */
fun ApplicationDsl.server(serverFactory: ConfigurableReactiveWebServerFactory = netty(), init: WebFluxServerDsl.() -> Unit =  {}) {
	initializers.add(WebFluxServerDsl(init, serverFactory))
}

/**
 * Configure a WebFlux client via a [dedicated DSL][WebFluxClientDsl].
 *
 * This DSL configures [WebFlux client](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux).
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 * 0..n clients are supported (you can specify the bean name to differentiate them.
 *
 * Require `org.springframework.boot:spring-boot-starter-webflux` dependency.
 *
 * @sample org.springframework.boot.kofu.samples.clientDsl
 * @see WebFluxClientDsl.codecs
 */
fun ApplicationDsl.client(baseUrl: String? = null, name: String? = null, init: WebFluxClientDsl.() -> Unit =  {}) {
	initializers.add(WebFluxClientDsl(init, baseUrl, name))
}
