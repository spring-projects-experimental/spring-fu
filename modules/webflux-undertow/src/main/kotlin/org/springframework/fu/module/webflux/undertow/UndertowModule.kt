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

package org.springframework.fu.module.webflux.undertow

import io.undertow.Undertow
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.fu.module.webflux.WebServer
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder

/**
 * @author Ruslan Ibragimov
 */
internal class UndertowModule(private val port: Int,
							  private val host: String): WebFluxModule.AbstractWebServerModule(port, host) {

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean {
			UndertowWebServer(port, host)
		}
	}
}

/**
 * @author Ruslan Ibragimov
 */
private class UndertowWebServer(private val port: Int = 8080,
								private val host: String = "0.0.0.0") : WebServer(port) {

	private var undertow: Undertow? = null

	/**
	 * This method relies here on internals of [Undertow].
	 * Specifically [Undertow.stop] method nullify some internals like [Undertow.xnio].
	 */
	override fun isRunning() = undertow?.xnio != null

	override fun start() {
		if (isRunning) {
			return
		}

		val httpHandler = WebHttpHandlerBuilder.applicationContext(context).build()
		undertow = Undertow.builder()
			.addHttpListener(port, host, UndertowHttpHandlerAdapter(httpHandler))
			.build()

		undertow?.start()
	}

	override fun stop(callback: Runnable) {
		undertow?.stop()
		callback.run()
	}

	override fun stop() {
		undertow?.stop()
	}

}

fun WebFluxModule.undertow(port: Int = 8080, host: String = "0.0.0.0") : WebFluxModule.WebServerModule = UndertowModule(port, host)
