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

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment

@DslMarker
annotation class ContainerModuleMarker

typealias Module = ApplicationContextInitializer<GenericApplicationContext>

@ContainerModuleMarker
abstract class ContainerModule(private val condition: (ConfigurableEnvironment) -> Boolean = { true }): Module {

	lateinit var context: GenericApplicationContext

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
open class ApplicationDsl(private val init: ApplicationDsl.() -> Unit, condition: (ConfigurableEnvironment) -> Boolean = { true }) : ContainerModule(condition) {

	val env : ConfigurableEnvironment
		get() = context.environment

	fun beans(init: BeanDefinitionDsl.() -> Unit) {
		modules.add(BeanDefinitionDsl(init))
	}

	/**
	 * Get a reference to the bean by type or type + name with the syntax
	 * `ref<Foo>()` or `ref<Foo>("foo")`. When leveraging Kotlin type inference
	 * it could be as short as `ref()` or `ref("foo")`.
	 * @param name the name of the bean to retrieve
	 * @param T type the bean must match, can be an interface or superclass
	 */
	inline fun <reified T : Any> ref(name: String? = null) : T = when (name) {
		null -> context.getBean(T::class.java)
		else -> context.getBean(name, T::class.java)
	}

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		init()
		super.initialize(context)
	}

	/**
	 * Take in account functional configuration enclosed in the provided lambda only when the
	 * specified profile is active.
	 */
	fun profile(profile: String, init: ApplicationDsl.() -> Unit) {
		modules.add(ApplicationDsl(init, { it.activeProfiles.contains(profile) }))
	}

	fun run(context: GenericApplicationContext = GenericApplicationContext(), await: Boolean = false) {
		context.registerBean("messageSource") {
			ReloadableResourceBundleMessageSource().apply {
				setBasename("messages")
				setDefaultEncoding("UTF-8")
			}
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

fun ApplicationDsl.configuration(init: (Environment) -> Any): Unit =
		beans {
			bean { init(env) }
		}

fun application(init: ApplicationDsl.() -> Unit): ApplicationDsl {
	val application = ApplicationDsl(init)
	application.init()
	return application
}
