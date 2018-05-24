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

import org.apache.catalina.LifecycleState
import org.apache.catalina.connector.Connector
import org.apache.catalina.core.StandardContext
import org.apache.catalina.loader.WebappClassLoader
import org.apache.catalina.loader.WebappLoader
import org.apache.catalina.startup.Tomcat
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.Lifecycle
import org.springframework.context.SmartLifecycle
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.http.server.reactive.TomcatHttpHandlerAdapter
import org.springframework.util.ClassUtils
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer
import reactor.ipc.netty.tcp.BlockingNettyContext

enum class Server {
    NETTY, TOMCAT
}

interface WebServer : SmartLifecycle

/**
 * @author Sebastien Deleuze
 */
class NettyWebServer(private val context: ApplicationContext, private val port: Int = 8080) : WebServer {

    private val server: HttpServer by lazy { HttpServer.create(port) }
    private var nettyContext: BlockingNettyContext? = null

	override fun isRunning() = nettyContext != null

	override fun start() {
		if (!isRunning) {
			val httpHandler = WebHttpHandlerBuilder.applicationContext(context).build()
			nettyContext = server.start(ReactorHttpHandlerAdapter(httpHandler))
		}
	}

	override fun stop(callback: Runnable) {
		nettyContext?.shutdown()
		callback.run()
	}

	override fun stop() {
		nettyContext?.shutdown()
	}

	override fun isAutoStartup() = true

	override fun getPhase() = Integer.MIN_VALUE
}

/**
 * @author Sebastien Deleuze
 */
class TomcatWebServer(private val context: ApplicationContext, private val port: Int = 8080) : WebServer {

	private val tomcat = Tomcat()


	override fun isRunning() = tomcat.server.state == LifecycleState.STARTED

	override fun start() {
		if (isRunning) {
			return
		}
		val httpHandler = WebHttpHandlerBuilder.applicationContext(context).build()
		val servlet = TomcatHttpHandlerAdapter(httpHandler)

		val docBase = createTempDir("tomcat-docbase")
		val context = StandardContext()
		context.path = ""
		context.docBase = docBase.absolutePath
		context.addLifecycleListener(Tomcat.FixContextListener())
		context.parentClassLoader = ClassUtils.getDefaultClassLoader()
		val loader = WebappLoader(context.parentClassLoader)
		loader.loaderClass = WebappClassLoader::class.java.name
		loader.delegate = true
		context.loader = loader

		Tomcat.addServlet(context, "httpHandlerServlet", servlet).isAsyncSupported = true
		context.addServletMappingDecoded("/", "httpHandlerServlet")
		tomcat.host.addChild(context)

		val baseDir = createTempDir("tomcat")
		tomcat.setBaseDir(baseDir.absolutePath)
		val connector = Connector("org.apache.coyote.http11.Http11NioProtocol")
		tomcat.service.addConnector(connector)
		connector.setProperty("bindOnInit", "false")
		connector.port = port
		tomcat.connector = connector
		tomcat.host.autoDeploy = false

		tomcat.server.start()
	}

	override fun stop(callback: Runnable) {
		tomcat.stop()
		callback.run()
	}

	override fun stop() {
		tomcat.stop()
	}

	override fun isAutoStartup() = true

	override fun getPhase() = Integer.MIN_VALUE

}
