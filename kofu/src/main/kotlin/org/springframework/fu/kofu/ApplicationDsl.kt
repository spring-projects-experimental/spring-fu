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

package org.springframework.fu.kofu

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean

/**
 * Kofu DSL for application configuration.
 *
 * @author Sebastien Deleuze
 * @see application
 */
open class ApplicationDsl internal constructor(private val startServer: Boolean, private val initApplication: ApplicationDsl.() -> Unit) : ConfigurationDsl() {

	internal class Application

	override fun register(context: GenericApplicationContext) {
		initApplication()

		context.registerBean("messageSource") {
			ReloadableResourceBundleMessageSource().apply {
				setBasename("messages")
				setDefaultEncoding("UTF-8")
			}
		}
	}

	/**
	 * @param args the application arguments (usually passed from a Java main method)
	 * @param profiles [ApplicationContext] profiles separated by commas.
	 */
	fun run(args: Array<String> = emptyArray(), profiles: String = "") {
		val application = object: SpringApplication(Application::class.java) {
			override fun load(context: ApplicationContext?, sources: Array<out Any>?) {
				// We don't want the annotation bean definition reader
			}
		}
		application.webApplicationType = if(startServer) WebApplicationType.REACTIVE else WebApplicationType.NONE
		application.setApplicationContextClass(
				if (startServer)
					ReactiveWebServerApplicationContext::class.java
				else
					GenericApplicationContext::class.java
		)
		if (!profiles.isEmpty()) {
			application.setAdditionalProfiles(*profiles.split(",").map { it.trim() }.toTypedArray())
		}
		application.addInitializers(this)
		System.setProperty("spring.backgroundpreinitializer.ignore", "true")
		application.run(*args)
	}

	fun stop() {
		context.close()
	}
}

/**
 * Define an [application Kofu configuration][ApplicationDsl] that allows to configure a Spring Boot
 * application using Kofu DSL and functional bean registration.
 *
 * Require `org.springframework.fu:spring-boot-kofu` dependency.
 *
 * @sample org.springframework.fu.kofu.samples.applicationDslWithCustomBeanApplication
 * @sample org.springframework.fu.kofu.samples.applicationDslOverview
 * @param startServer Define if Spring Boot should start a web server or not
 * @param dsl The `application { }` DSL
 * @see ApplicationDsl.logging
 * @see org.springframework.fu.kofu.web.server
 * @see org.springframework.fu.kofu.web.client
 * @see org.springframework.fu.kofu.mongo.mongodb
 */
fun application(startServer: Boolean = true, dsl: ApplicationDsl.() -> Unit)
		= ApplicationDsl(startServer, dsl)
