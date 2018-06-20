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

package org.springframework.fu.module.webflux.netty

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.fu.module.webflux.WebServer
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Sebastien Deleuze
 */
internal class NettyModule(private val port: Int = 8080): WebFluxModule.AbstractWebServerModule(port) {

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean {
			NettyWebServer(port)
		}
	}
}

/**
 * @author Sebastien Deleuze
 */
private class NettyWebServer(private val port: Int) : WebServer(port) {

	private val server: HttpServer by lazy { HttpServer.create().tcpConfiguration { it.host("0.0.0.0") }.port(port) }
	private val disposableServer = AtomicReference<DisposableServer>()

	override fun isRunning() = !(disposableServer.get()?.isDisposed ?: true )

	override fun start() {
		if (!isRunning) {
			val httpHandler = WebHttpHandlerBuilder.applicationContext(context).build()
			disposableServer.set(server.handle(ReactorHttpHandlerAdapter(httpHandler)).bindNow())
		}
	}

	override fun stop(callback: Runnable) {
		val disposableServer = this.disposableServer.get()
		if (disposableServer != null) {
			disposableServer.disposeNow()
			callback.run()
		}
	}

	override fun stop() {
		disposableServer.get()?.disposeNow()
	}

}

fun WebFluxModule.netty(port: Int = 8080) : WebFluxModule.WebServerModule = NettyModule(port)