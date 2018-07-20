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
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.SmartLifecycle
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.core.codec.*
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.fu.ApplicationDsl
import org.springframework.fu.AbstractModule
import org.springframework.fu.Module
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

		private val routes = mutableListOf<() -> RouterFunction<ServerResponse>>()

		override fun initialize(context: GenericApplicationContext) {
			if (context.containsBeanDefinition("webHandler")) {
				throw IllegalStateException("Only one server per application is supported")
			}
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
									builder.codecs { codec.invoke(it) }
								}
							}
						}
					}

					try {
						builder.viewResolver(ref())
					}
					catch (ex: NoSuchBeanDefinitionException) {}
					val router = if (!routes.isEmpty()) {
						routes.map { it() }.reduce(RouterFunction<ServerResponse>::and)
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

		fun router(routes: (RouterFunctionDsl.() -> Unit)) {
			this.routes.add(RouterFunctionDsl(routes))
		}

		fun include(router: () -> RouterFunction<ServerResponse>) {
			this.routes.add(router)
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

