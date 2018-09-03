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

package org.springframework.fu

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean
import org.springframework.fu.properties.ConfigurationPropertiesBinder
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor



/**
 * @author Sebastien Deleuze
 */
open class ApplicationDsl(private val startServer: Boolean, val init: ApplicationDsl.() -> Unit) : AbstractModule() {

	internal class Application

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		context.registerBean(AutowiredAnnotationBeanPostProcessor::class.java)

		context.registerBean("messageSource") {
			ReloadableResourceBundleMessageSource().apply {
				setBasename("messages")
				setDefaultEncoding("UTF-8")
			}
		}
		init()
		super.initialize(context)
	}

	fun beans(init: BeanDefinitionDsl.() -> Unit) {
		initializers.add(BeanDefinitionDsl(init))
	}

	fun logging(init: LoggingDsl.() -> Unit): LoggingDsl = LoggingDsl(init)

	inline fun <reified T : Any> configuration(prefix: String = "") {
		context.registerBean("${T::class.java.simpleName.toLowerCase()}ConfigurationProperties") {
			ConfigurationPropertiesBinder(context).bind(prefix, Bindable.of(T::class.java)).get()
		}
	}

	inline fun <reified E : ApplicationEvent>listener(crossinline listener: (E) -> Unit) {
		context.registerBean {
			// TODO Leverage SPR-16872 when it will be fixed
			if (it is E) {
				ApplicationListener<E> {
					listener.invoke(it)
				}
			}
		}
	}

	/**
	 * Take in account functional configuration enclosed in the provided lambda only when the
	 * specified profile is active.
	 */
	fun profile(profile: String, init: () -> Unit) {
		if (env.activeProfiles.contains(profile)) {
			init()
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
		application.setRegisterShutdownHook(false)
		application.run(*args)
	}

	fun stop() {
		context.close()
	}
}

fun application(isServer: Boolean = true, init: ApplicationDsl.() -> Unit) = ApplicationDsl(isServer, init)
