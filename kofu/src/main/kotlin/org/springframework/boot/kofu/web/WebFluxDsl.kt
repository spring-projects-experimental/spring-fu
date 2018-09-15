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
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientInitializer
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.reactive.*
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
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.WebFilter

class WebFluxCodecsDsl : AbstractDsl() {

	override fun register(context: GenericApplicationContext) {
	}

	fun string() {
		initializers.add(StringCodecInitializer())
	}

	fun resource() {
		initializers.add(ResourceCodecInitializer())
	}
}

open class WebFluxServerDsl(private val init: WebFluxServerDsl.() -> Unit,
							private val serverFactory: ConfigurableReactiveWebServerFactory): AbstractDsl() {

	private val serverProperties = ServerProperties()

	private val resourceProperties = ResourceProperties()

	private val webFluxProperties = WebFluxProperties()

	override fun register(context: GenericApplicationContext) {
		init()
		if (context.containsBeanDefinition("webHandler")) {
			throw IllegalStateException("Only one server per application is supported")
		}
		ReactiveWebServerInitializer(serverProperties, resourceProperties, webFluxProperties, serverFactory).initialize(context)
	}

	fun codecs(init: WebFluxCodecsDsl.() -> Unit =  {}) {
		val codecModule = WebFluxCodecsDsl()
		codecModule.init()
		initializers.add(codecModule)
	}

	fun filter(filter: WebFilter) {
		initializers.add(ApplicationContextInitializer { it.registerBean { filter } })
	}

	fun router(routes: (RouterFunctionDsl.() -> Unit)) {
		initializers.add(ApplicationContextInitializer { it.registerBean { RouterFunctionDsl(routes).invoke() } })
	}

	fun include(router: () -> RouterFunction<ServerResponse>) {
		initializers.add(ApplicationContextInitializer { it.registerBean { router.invoke() } })
	}

}

class WebFluxClientDsl(private val init: WebFluxClientDsl.() -> Unit, val baseUrl: String?, private val name: String?) : AbstractDsl() {

	override fun register(context: GenericApplicationContext) {
		init()
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

	fun codecs(init: WebFluxCodecsDsl.() -> Unit =  {}) {
		val codecModule = WebFluxCodecsDsl()
		codecModule.init()
		initializers.add(codecModule)
	}
}

fun ApplicationDsl.netty(port: Int = 8080) = NettyReactiveWebServerFactory(port)

fun ApplicationDsl.tomcat(port: Int = 8080) = TomcatReactiveWebServerFactory(port)

fun ApplicationDsl.undertow(port: Int = 8080) = UndertowReactiveWebServerFactory(port)

fun ApplicationDsl.jetty(port: Int = 8080) = JettyReactiveWebServerFactory(port)

fun ApplicationDsl.server(serverFactory: ConfigurableReactiveWebServerFactory, init: WebFluxServerDsl.() -> Unit =  {}) {
	initializers.add(WebFluxServerDsl(init, serverFactory))
}

fun ApplicationDsl.client(baseUrl: String? = null, name: String? = null, init: WebFluxClientDsl.() -> Unit =  {}) {
	initializers.add(WebFluxClientDsl(init, baseUrl, name))
}
