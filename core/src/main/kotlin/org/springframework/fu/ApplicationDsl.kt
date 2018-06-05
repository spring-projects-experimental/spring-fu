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
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean
import org.springframework.core.env.Environment
import java.util.function.Supplier

@DslMarker
annotation class ContainerModuleMarker

typealias Module = ApplicationContextInitializer<GenericApplicationContext>

interface ModuleRef: ApplicationContextInitializer<GenericApplicationContext> {

	var context: GenericApplicationContext

	val env : Environment
		get() = context.environment

}

/**
 * Get a reference to the bean by type or type + name with the syntax
 * `ref<Foo>()` or `ref<Foo>("foo")`. When leveraging Kotlin type inference
 * it could be as short as `ref()` or `ref("foo")`.
 * @param name the name of the bean to retrieve
 * @param T type the bean must match, can be an interface or superclass
 */
inline fun <reified T : Any> ModuleRef.ref(name: String? = null) : T = when (name) {
	null -> context.getBean(T::class.java)
	else -> context.getBean(name, T::class.java)
}

@ContainerModuleMarker
abstract class ContainerModule(private val condition: (Environment) -> Boolean = { true }): ModuleRef {

	override lateinit var context: GenericApplicationContext

	val modules = mutableListOf<Module>()

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		for (child in modules) {
			if ((child is ContainerModule && child.condition.invoke(context.environment)) || child !is ContainerModule) {
				child.initialize(context)
			}
		}
	}
}

/**
 * @author Sebastien Deleuze
 */
open class ApplicationDsl(private val init: ApplicationDsl.() -> Unit) : ContainerModule() {

	private val beanDsl = BeanDefinitionDsl({})

	/**
	 * Take in account functional configuration enclosed in the provided lambda only when the
	 * specified profile is active.
	 */
	fun profile(profile: String, init: ApplicationDsl.() -> Unit) {
		if (env.activeProfiles.contains(profile)) {
			modules.add(ApplicationDsl(init))
		}
	}

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
									  autowireMode: BeanDefinitionDsl.Autowire = BeanDefinitionDsl.Autowire.CONSTRUCTOR,
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
									  autowireMode: BeanDefinitionDsl.Autowire = BeanDefinitionDsl.Autowire.NO,
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


	fun <T : Any> configuration(module: ConfigurationModule<T>) = modules.add(module)

	inline fun <reified T : Any> configuration(noinline init: ConfigurationModule<*>.() -> T) = modules.add(ConfigurationModule(init, T::class.java))

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		init()
		super.initialize(context)
	}

	/**
	 * @param context the [GenericApplicationContext] instance to use
	 * @param await set to `true` to block, useful when used in a `main()` function
	 * @param profiles [ApplicationContext] profiles separated by commas.
	 */
	fun run(context: GenericApplicationContext = GenericApplicationContext(), await: Boolean = false, profiles: String = "") {
		context.registerBean("messageSource") {
			ReloadableResourceBundleMessageSource().apply {
				setBasename("messages")
				setDefaultEncoding("UTF-8")
			}
		}
		if (!profiles.isEmpty()) {
			context.environment.setActiveProfiles(*profiles.split(",").map { it.trim() }.toTypedArray())
		}
		initialize(context)
		context.refresh()
		if (await) {
			while (true)
			{
				Thread.sleep(100)
			}
		}
	}

}

open class ConfigurationModule<T : Any>(private val init: ConfigurationModule<T>.() -> T, private val clazz: Class<T>): ContainerModule() {

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		context.registerBean(clazz, Supplier { init() })
	}

}

inline fun <reified T : Any> configuration(noinline init: ConfigurationModule<*>.() -> T) = ConfigurationModule(init, T::class.java)

fun application(init: ApplicationDsl.() -> Unit) = ApplicationDsl(init)
