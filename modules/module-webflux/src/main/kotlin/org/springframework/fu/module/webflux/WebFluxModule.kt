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
import org.springframework.context.support.beans
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.fu.ApplicationDsl
import org.springframework.fu.ContainerModule
import org.springframework.fu.Module
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler
import org.springframework.web.server.WebFilter
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver
import reactor.core.publisher.Mono
import java.util.function.Consumer

/**
 * @author Sebastien Deleuze
 */
class WebFluxModule: ContainerModule() {

	fun server(server: Server, port: Int = 8080, init: WebFluxServerModule.() -> Unit =  {}) {
		initModule(WebFluxServerModule(server, port), init)
	}

	fun client() = initializers.add(beans { bean { WebClient.create() } })

	class WebFluxServerModule(private val server: Server,
							  private val port: Int): ContainerModule() {

		private val builder = HandlerStrategies.empty()

		init {
			initializers.add(beans {
				bean("webHandler") {
					builder.exceptionHandler(WebFluxResponseStatusExceptionHandler())
					builder.localeContextResolver(AcceptHeaderLocaleContextResolver())
					for (c in children) {
						if (c is WebFluxCodecsModule) {
							for (codec in c.children) {
								builder.codecs({ (codec as WebFluxCodecModule).invoke(it) } )
							}
						}
					}

					try {
						builder.viewResolver(ref())
					}
					catch (ex: NoSuchBeanDefinitionException) {}
					RouterFunctions.toWebHandler(ref(), builder.build())
				}
				bean {
					val routers = context.getBeansOfType<RouterFunction<ServerResponse>>()
					if (!routers.isEmpty()) {
						routers.values.reduce(RouterFunction<ServerResponse>::and)
					}
					else {
						RouterFunction<ServerResponse> { Mono.empty() }
					}
				}
				bean {
					when (server) {
						Server.NETTY -> NettyWebServer(context, port)
						Server.TOMCAT -> TomcatWebServer(context, port)
					}
				}
			})
		}

		fun codecs(init: WebFluxCodecsModule.() -> Unit =  {}) {
			initModule(WebFluxCodecsModule(builder), init)
		}

		fun filter(filter: WebFilter) {
			builder.webFilter(filter)
		}

		fun routes(router: RouterFunctionDsl.() -> Unit) =
				initializers.add(beans { bean { router(router) } })

		fun routes(router: RouterFunction<ServerResponse>) =
				initializers.add(beans { bean { router } })

	}

	class WebFluxCodecsModule(val builder: HandlerStrategies.Builder): ContainerModule()

	interface WebFluxCodecModule: Module, (ServerCodecConfigurer) -> (Unit)
}

fun ApplicationDsl.webflux(init: WebFluxModule.() -> Unit): WebFluxModule {
	val webFluxDsl = WebFluxModule()
	initModule(webFluxDsl, init)
	return webFluxDsl
}
