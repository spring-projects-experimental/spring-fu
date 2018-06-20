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

package org.springframework.fu.module.webflux.tomcat

import org.apache.catalina.LifecycleState
import org.apache.catalina.connector.Connector
import org.apache.catalina.core.StandardContext
import org.apache.catalina.loader.WebappClassLoader
import org.apache.catalina.loader.WebappLoader
import org.apache.catalina.startup.Tomcat
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.module.webflux.WebFluxModule

import org.springframework.fu.module.webflux.WebServer
import org.springframework.http.server.reactive.TomcatHttpHandlerAdapter
import org.springframework.util.ClassUtils
import org.springframework.web.server.adapter.WebHttpHandlerBuilder

/**
 * @author Sebastien Deleuze
 */
internal class TomcatModule(private val port: Int): WebFluxModule.AbstractWebServerModule(port) {

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean {
			TomcatWebServer(port)
		}
	}
}

/**
 * @author Sebastien Deleuze
 */
private class TomcatWebServer(private val port: Int) : WebServer(port) {

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

}

fun WebFluxModule.tomcat(port: Int = 8080) : WebFluxModule.WebServerModule = TomcatModule(port)
