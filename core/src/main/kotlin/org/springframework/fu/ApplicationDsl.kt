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
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean

/**
 * @author Sebastien Deleuze
 */
open class ApplicationDsl(private val isServer: Boolean, val init: ApplicationDsl.() -> Unit) : AbstractModule() {

	internal class Application

	fun beans(init: BeanDefinitionDsl.() -> Unit) {
		initializers.add(BeanDefinitionDsl(init))
	}

	fun <T : Any> configuration(module: ConfigurationModule<T>) = initializers.add(module)

	inline fun <reified T : Any> configuration(noinline init: ConfigurationModule<*>.() -> T) = initializers.add(ConfigurationModule(init, T::class.java))

	fun logging(init: LoggingDsl.() -> Unit): LoggingDsl = LoggingDsl(init)


	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		init()
		context.registerBean("messageSource") {
			ReloadableResourceBundleMessageSource().apply {
				setBasename("messages")
				setDefaultEncoding("UTF-8")
			}
		}
		super.initialize(context)
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
	 * @param await set to `true` to block, useful when used in a `main()` function
	 * @param profiles [ApplicationContext] profiles separated by commas.
	 */
	fun run(args: Array<String> = emptyArray(), await: Boolean = false, profiles: String = "") {
		val application = SpringApplication(Application::class.java)
		application.webApplicationType = if(isServer) WebApplicationType.REACTIVE else WebApplicationType.NONE
		application.setApplicationContextClass(
				if (isServer)
					ReactiveWebServerApplicationContext::class.java
				else
					GenericApplicationContext::class.java
		)
		if (!profiles.isEmpty()) {
			application.setAdditionalProfiles(*profiles.split(",").map { it.trim() }.toTypedArray())
		}
		application.addInitializers(this)
		application.run(*args)
		if (await) {
			while (true)
			{
				Thread.sleep(100)
			}
		}
	}

	fun stop() {
		context.close()
	}
}

fun application(isServer: Boolean = true, init: ApplicationDsl.() -> Unit) = ApplicationDsl(isServer, init)
