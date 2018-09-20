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

package org.springframework.boot.kofu

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.properties.FunctionalConfigurationPropertiesBinder
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean
import java.lang.reflect.Constructor


/**
 * Kofu DSL for application configuration.
 *
 * @author Sebastien Deleuze
 * @see application
 */
open class ApplicationDsl internal constructor(private val startServer: Boolean, internal val init: ApplicationDsl.() -> Unit) : AbstractDsl() {

	internal class Application

	override fun register(context: GenericApplicationContext) {
		init()
		context.registerBean(AutowiredConstructorBeanPostProcessor::class.java)
		context.registerBean("messageSource") {
			ReloadableResourceBundleMessageSource().apply {
				setBasename("messages")
				setDefaultEncoding("UTF-8")
			}
		}
	}

	class AutowiredConstructorBeanPostProcessor: AutowiredAnnotationBeanPostProcessor() {

		@Suppress("UNCHECKED_CAST")
		override fun determineCandidateConstructors(beanClass: Class<*>, beanName: String): Array<Constructor<*>>? {
			val primaryConstructor = BeanUtils.findPrimaryConstructor(beanClass)
			return if (primaryConstructor != null) arrayOf(primaryConstructor) else null
		}
	}

	/**
	 * Configure beans via a [dedicated DSL][BeanDefinitionDsl].
	 *
	 * Since classes with a single constructor have their parameters automatically autowired,
	 * it is recommended to use constructor injection with `val` read-only
	 * (and non-nullable when possible) private [properties](https://kotlinlang.org/docs/reference/properties.html).
	 *
	 * @sample org.springframework.boot.kofu.samples.beansDsl
	 */
	fun beans(dsl: BeanDefinitionDsl.() -> Unit) {
		initializers.add(BeanDefinitionDsl(dsl))
	}

	/**
	 * Specify the class and the optional prefux of configuration properties, which is the
	 * same mechanism than regular
	 * [Spring Boot configuration properties mechanism](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties),
	 * without the need to use `@ConfigurationProperties` annotation.
	 *
	 * @sample org.springframework.boot.kofu.samples.properties
	 * @sample org.springframework.boot.kofu.samples.SampleProperties
	 */
	inline fun <reified T : Any> properties(prefix: String = "") {
		context.registerBean("${T::class.java.simpleName.toLowerCase()}ConfigurationProperties") {
			FunctionalConfigurationPropertiesBinder(context).bind(prefix, Bindable.of(T::class.java)).get()
		}
	}

	/**
	 * Configure global, package or class logging via a [dedicated DSL][LoggingDsl].
	 * @sample org.springframework.boot.kofu.samples.loggingDsl
	 */
	fun logging(dsl: LoggingDsl.() -> Unit) {
		LoggingDsl(dsl)
	}

	/**
	 * Declare application event Listeners in order to run tasks when `ApplicationContextEvent`
	 * like `ApplicationReadyEvent` are emitted.
	 * @sample org.springframework.boot.kofu.samples.listener
	 */
	inline fun <reified E : ApplicationEvent>listener(crossinline listener: (E) -> Unit) {
		context.addApplicationListener {
			// TODO Leverage SPR-16872 when it will be fixed
			if (it is E) {
				listener.invoke(it)
			}
		}
	}

	/**
	 * Take in account functional properties enclosed in the provided lambda only when the
	 * specified profile is active.
	 */
	fun profile(profile: String, block: () -> Unit) {
		if (env.activeProfiles.contains(profile)) {
			block()
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
 * @sample org.springframework.boot.kofu.samples.applicationDslWithCustomBeanApplication
 * @sample org.springframework.boot.kofu.samples.applicationDslOverview
 * @param startServer Define if Spring Boot should start a web server or not
 * @param dsl The `application { }` DSL
 * @see ApplicationDsl.logging
 * @see org.springframework.boot.kofu.web.server
 * @see org.springframework.boot.kofu.web.client
 * @see org.springframework.boot.kofu.mongo.mongodb
 */
fun application(startServer: Boolean = true, dsl: ApplicationDsl.() -> Unit)
		= ApplicationDsl(startServer, dsl)
