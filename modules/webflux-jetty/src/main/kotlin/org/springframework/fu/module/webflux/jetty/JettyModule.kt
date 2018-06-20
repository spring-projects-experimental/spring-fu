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

package org.springframework.fu.module.webflux.jetty

import org.eclipse.jetty.server.Server
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.fu.module.webflux.WebServer
import java.net.InetSocketAddress
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.JettyHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder


/**
 * @author Alexey Nesterov
 */

private const val DEFAULT_STOP_TIMEOUT: Long = 5000

internal class JettyModule(
        private val port: Int,
        private val host: String) : WebFluxModule.AbstractWebServerModule(port, host) {

    override fun initialize(context: GenericApplicationContext) {
        context.registerBean {
            JettyServerModule(port, host)
        }
    }
}

private class JettyServerModule(private val port: Int, private val host: String) : WebServer(port) {

    private var contextHandler: ServletContextHandler? = null
    private val logger = LoggerFactory.getLogger(JettyServerModule::class.java)

    private val jettyServer: Server by lazy {
        Server(InetSocketAddress(host, port))
    }

    override fun isRunning() = jettyServer.isRunning && this.contextHandler?.isRunning ?: false

    override fun start() {
        if (!isRunning) {
            val httpHandler = WebHttpHandlerBuilder.applicationContext(context).build()

            val servlet = JettyHttpHandlerAdapter(httpHandler)
            val servletHolder = ServletHolder(servlet).apply {
                isAsyncSupported = true
            }

            this.contextHandler = ServletContextHandler(this.jettyServer, "", false, false).apply {
                addServlet(servletHolder, "/")
                start()
            }

            val connector = ServerConnector(this.jettyServer).apply {
                host = host
                port = port
            }

            this.jettyServer.addConnector(connector)

            jettyServer.start()
        }
    }

    override fun stop(callback: Runnable) {
        this.stop()
        callback.run()
    }

    override fun stop() {
        try {
            this.contextHandler?.stop()
        } finally {
            stopInternal()
        }
    }

    private fun stopInternal() {
        try {
            if (this.jettyServer.isRunning) {
                this.jettyServer.stopTimeout = DEFAULT_STOP_TIMEOUT
                this.jettyServer.stop()
                this.jettyServer.destroy()
            }
        } catch (ex: Exception) {
            logger.error("Failed to stop jetty server", ex)
        }
    }
}

fun WebFluxModule.jetty(port: Int = 8080, host: String = "0.0.0.0"): WebFluxModule.WebServerModule = JettyModule(port, host)