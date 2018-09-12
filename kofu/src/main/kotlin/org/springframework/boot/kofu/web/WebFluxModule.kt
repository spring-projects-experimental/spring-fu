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

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.mustache.MustacheProperties
import org.springframework.boot.autoconfigure.mustache.registerMustacheConfiguration
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties
import org.springframework.boot.autoconfigure.web.reactive.registerReactiveWebServerConfiguration
import org.springframework.boot.kofu.AbstractModule
import org.springframework.boot.kofu.ApplicationDsl
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.context.support.registerBean
import org.springframework.core.codec.*
import org.springframework.http.codec.CodecConfigurer
import org.springframework.http.codec.HttpMessageReader
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.http.codec.ResourceHttpMessageWriter
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.WebFilter

class WebFluxCodecsModule : AbstractModule() {


	internal val encoders = mutableListOf<Encoder<*>>()

	internal val decoders = mutableListOf<Decoder<*>>()

	internal val writers = mutableListOf<HttpMessageWriter<*>>()

	internal val readers = mutableListOf<HttpMessageReader<*>>()

	override fun registerBeans(context: GenericApplicationContext) {
		context.registerBean<BeanPostProcessor> {
			object : BeanPostProcessor {
				override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
					if (bean is CodecConfigurer) {
						with(bean.customCodecs()) {
							encoders.forEach { this.encoder(it) }
							decoders.forEach { this.decoder(it) }
							writers.forEach { this.writer(it) }
							readers.forEach { this.reader(it) }
						}
					}
					return bean
				}
			}
		}
	}

	fun string() {
		encoders.add(CharSequenceEncoder.textPlainOnly())
		decoders.add(StringDecoder.textPlainOnly())
	}

	fun resource() {
		writers.add(ResourceHttpMessageWriter())
		decoders.add(ResourceDecoder())
	}
}

open class WebFluxServerModule(private val init: WebFluxServerModule.() -> Unit,
							   private val serverFactory: ConfigurableReactiveWebServerFactory): AbstractModule() {

	private val serverProperties = ServerProperties()

	private val resourceProperties = ResourceProperties()

	private val webFluxProperties = WebFluxProperties()

	override fun registerBeans(context: GenericApplicationContext) {
		init()
		if (context.containsBeanDefinition("webHandler")) {
			throw IllegalStateException("Only one server per application is supported")
		}
		registerReactiveWebServerConfiguration(context, serverProperties, resourceProperties, webFluxProperties, serverFactory)
	}

	fun codecs(init: WebFluxCodecsModule.() -> Unit =  {}) {
		val codecModule = WebFluxCodecsModule()
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

class WebFluxClientModule(private val init: WebFluxClientModule.() -> Unit, val baseUrl: String?, private val name: String?) : AbstractModule() {

	private val clientBuilder = WebClient.builder()

	override fun registerBeans(context: GenericApplicationContext) {
		init()
		// TODO Fix registerBean extension signature to accept null names
		if (name != null)
			context.registerBean(name) { client() }
		else
			context.registerBean { client() }
	}

	private fun client() : WebClient {
		if (baseUrl != null) {
			clientBuilder.baseUrl(baseUrl)
		}
		val exchangeStrategiesBuilder = ExchangeStrategies.builder()
		clientBuilder.exchangeStrategies(exchangeStrategiesBuilder.build())
		return clientBuilder.build()
	}

	fun codecs(init: WebFluxCodecsModule.() -> Unit =  {}) {
		val codecModule = WebFluxCodecsModule()
		codecModule.init()
		initializers.add(codecModule)
	}
}

fun ApplicationDsl.netty(port: Int = 8080) = NettyReactiveWebServerFactory(port)

fun ApplicationDsl.tomcat(port: Int = 8080) = TomcatReactiveWebServerFactory(port)

fun ApplicationDsl.undertow(port: Int = 8080) = UndertowReactiveWebServerFactory(port)

fun ApplicationDsl.jetty(port: Int = 8080) = JettyReactiveWebServerFactory(port)

fun ApplicationDsl.server(serverFactory: ConfigurableReactiveWebServerFactory, init: WebFluxServerModule.() -> Unit =  {}) {
	initializers.add(WebFluxServerModule(init, serverFactory))
}

fun ApplicationDsl.client(baseUrl: String? = null, name: String? = null, init: WebFluxClientModule.() -> Unit =  {}) {
	initializers.add(WebFluxClientModule(init, baseUrl, name))
}
