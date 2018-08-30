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

import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.BeanDefinitionDsl.Autowire
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean
import java.util.function.Supplier

/**
 * @author Sebastien Deleuze
 */
open class SpringApplicationDsl(private val isServer: Boolean,  val init: SpringApplicationDsl.() -> Unit) : AbstractModule() {

	internal class Application

	/**
	 * Declare a bean definition from the given bean class which can be inferred when possible.
	 *
	 * @param name the name of the bean
	 * @param scope Override the target scope of this bean, specifying a new scope name.
	 * @param isLazyInit Set whether this bean should be lazily initialized.
	 * @param isPrimary Set whether this bean is a primary autowire candidate.
	 * @param autowireMode Set the autowire mode, `Autowire.CONSTRUCTOR` by default
	 * @param isAutowireCandidate Set whether this bean is a candidate for getting
	 * autowired into some other bean.
	 * @see GenericApplicationContext.registerBean
	 * @see org.springframework.beans.factory.config.BeanDefinition
	 */
	inline fun <reified T : Any> bean(name: String? = null,
									  scope: BeanDefinitionDsl.Scope? = null,
									  isLazyInit: Boolean? = null,
									  isPrimary: Boolean? = null,
									  autowireMode: Autowire = Autowire.CONSTRUCTOR,
									  isAutowireCandidate: Boolean? = null) {

		val customizer = BeanDefinitionCustomizer { bd ->
			scope?.let { bd.scope = scope.name.toLowerCase() }
			isLazyInit?.let { bd.isLazyInit = isLazyInit }
			isPrimary?.let { bd.isPrimary = isPrimary }
			isAutowireCandidate?.let { bd.isAutowireCandidate = isAutowireCandidate }
			if (bd is AbstractBeanDefinition) {
				bd.autowireMode = autowireMode.ordinal
			}
		}

		when (name) {
			null -> context.registerBean(T::class.java, customizer)
			else -> context.registerBean(name, T::class.java, customizer)
		}

	}

	/**
	 * Declare a bean definition using the given supplier for obtaining a new instance.
	 *
	 * @param name the name of the bean
	 * @param scope Override the target scope of this bean, specifying a new scope name.
	 * @param isLazyInit Set whether this bean should be lazily initialized.
	 * @param isPrimary Set whether this bean is a primary autowire candidate.
	 * @param autowireMode Set the autowire mode, `Autowire.NO` by default
	 * @param isAutowireCandidate Set whether this bean is a candidate for getting
	 * autowired into some other bean.
	 * @param function the bean supplier function
	 * @see GenericApplicationContext.registerBean
	 * @see org.springframework.beans.factory.config.BeanDefinition
	 */
	inline fun <reified T : Any> bean(name: String? = null,
									  scope: BeanDefinitionDsl.Scope? = null,
									  isLazyInit: Boolean? = null,
									  isPrimary: Boolean? = null,
									  autowireMode: Autowire = Autowire.NO,
									  isAutowireCandidate: Boolean? = null,
									  crossinline function: () -> T) {

		val customizer = BeanDefinitionCustomizer { bd ->
			scope?.let { bd.scope = scope.name.toLowerCase() }
			isLazyInit?.let { bd.isLazyInit = isLazyInit }
			isPrimary?.let { bd.isPrimary = isPrimary }
			isAutowireCandidate?.let { bd.isAutowireCandidate = isAutowireCandidate }
			if (bd is AbstractBeanDefinition) {
				bd.autowireMode = autowireMode.ordinal
			}
		}
		when (name) {
			null -> context.registerBean(T::class.java,
					Supplier { function.invoke() }, customizer)
			else -> context.registerBean(name, T::class.java,
					Supplier { function.invoke() }, customizer)
		}
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
	fun profile(profile: String, init: SpringApplicationDsl.() -> Unit) {
		if (env.activeProfiles.contains(profile)) {
			// TODO Avoid using application here
			initializers.add(SpringApplicationDsl(false, init))
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

fun application(isServer: Boolean = true, init: SpringApplicationDsl.() -> Unit) = SpringApplicationDsl(isServer, init)
