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

import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.reactive.*
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientBuilderInitializer
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
import org.springframework.web.function.server.CoroutinesRouterFunctionDsl
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.RouterFunctionDsl
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
open class WebFluxServerDsl(private val init: WebFluxServerDsl.() -> Unit): AbstractDsl() {

	private val serverProperties = ServerProperties()

	private val resourceProperties = ResourceProperties()

	private val webFluxProperties = WebFluxProperties()

	private var codecsConfigured: Boolean = false

	/**
	 * Define the listening port of the server.
	 */
	var port: Int = 8080

	/**
	 * Define the underlying engine used.
	 *
	 * @see netty
	 * @see tomcat
	 * @see jetty
	 * @ßee undertow
	 */
	var engine: ConfigurableReactiveWebServerFactory? = null


	override fun register(context: GenericApplicationContext) {
		init()
		if (engine == null) {
			engine = netty()
		}
		engine!!.setPort(port)

		if (!codecsConfigured) {
			initializers.add(StringCodecInitializer(false))
			initializers.add(ResourceCodecInitializer(false))
		}
		if (context.containsBeanDefinition("webHandler")) {
			throw IllegalStateException("Only one server per application is supported")
		}
		initializers.add(ReactiveWebServerInitializer(serverProperties, resourceProperties, webFluxProperties, engine))
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
		initializers.add(ApplicationContextInitializer { it.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { filter } })
	}

	/**
	 * Configure routes via a [dedicated DSL][RouterFunctionDsl].
	 * @sample org.springframework.boot.kofu.samples.routerDsl
	 */
	fun router(routes: (RouterFunctionDsl.() -> Unit)) {
		initializers.add(ApplicationContextInitializer { it.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { RouterFunctionDsl(routes).invoke() } })
	}

	/**
	 * Configure Coroutines routes via a [dedicated DSL][CoroutinesRouterFunctionDsl].
	 * @sample org.springframework.boot.kofu.samples.coRouterDsl
	 */
	fun coRouter(routes: (CoroutinesRouterFunctionDsl.() -> Unit)) {
		initializers.add(ApplicationContextInitializer { it.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(CoroutinesRouterFunctionDsl::class.java.name, context)) { CoroutinesRouterFunctionDsl(routes).invoke() } })
	}

	companion object {
		fun netty(): ConfigurableReactiveWebServerFactory = NettyReactiveWebServerFactory()
	}
}

/**
 * Kofu DSL for WebFlux client configuration.
 * @author Sebastien Deleuze
 */
class WebFluxClientBuilderDsl(internal val baseUrl: String?, private val init: WebFluxClientBuilderDsl.() -> Unit) : AbstractDsl() {

	private var codecsConfigured: Boolean = false

	override fun register(context: GenericApplicationContext) {
		init()
		if (!codecsConfigured) {
			initializers.add(StringCodecInitializer(true))
			initializers.add(ResourceCodecInitializer(true))
		}
		ReactiveWebClientBuilderInitializer(baseUrl).initialize(context)
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

/**
 * Configure a WebFlux server via a via a [dedicated DSL][WebFluxServerDsl].
 *
 * This DSL configures [WebFlux server](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux).
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 * [ApplicationDsl.startServer] needs to be set to `true` (it is by default).
 *
 * You can chose the underlying engine via the [serverFactory] parameter.
 *
 * Require `org.springframework.boot:spring-boot-starter-webflux` dependency.
 *
 * @param serverFactory The underlying web server to use: [netty] (the default), [tomcat], [jetty] or [undertow]
 * @sample org.springframework.boot.kofu.samples.routerDsl
 * @see WebFluxServerDsl.router
 * @see WebFluxServerDsl.coRouter
 * @see WebFluxServerDsl.codecs
 * @see WebFluxServerDsl.cors
 * @see WebFluxServerDsl.mustache
 */
fun ApplicationDsl.server(init: WebFluxServerDsl.() -> Unit =  {}) {
	initializers.add(WebFluxServerDsl(init))
}

/**
 * Configure a [WebFlux client](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-client) builder ([WebClient.builder]) via a [dedicated DSL][WebFluxClientBuilderDsl].
 *
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 *
 * Require `org.springframework.boot:spring-boot-starter-webflux` dependency.
 *
 * @param baseUrl The default base URL
 * @param dsl The [WebFlux client](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-client) builder ([WebClient.builder]) DSL
 * @sample org.springframework.boot.kofu.samples.clientDsl
 * @see WebFluxClientBuilderDsl.codecs
 */
fun ApplicationDsl.client(baseUrl: String? = null, dsl: WebFluxClientBuilderDsl.() -> Unit =  {}) {
	initializers.add(WebFluxClientBuilderDsl(baseUrl, dsl))
}
