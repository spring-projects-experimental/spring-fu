package org.springframework.boot

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.env.Environment

@DslMarker
annotation class ModuleMarker

@ModuleMarker
interface Module : ApplicationContextInitializer<GenericApplicationContext> {

	var context: GenericApplicationContext

	val env: Environment
		get() = context.environment

	val profiles: Array<String>
		get() = env.activeProfiles
}

/**
 * Get a reference to the bean by type or type + name with the syntax
 * `ref<Foo>()` or `ref<Foo>("foo")`. When leveraging Kotlin type inference
 * it could be as short as `ref()` or `ref("foo")`.
 * @param name the name of the bean to retrieve
 * @param T type the bean must match, can be an interface or superclass
 */
inline fun <reified T : Any> Module.ref(name: String? = null): T = when (name) {
	null -> context.getBean(T::class.java)
	else -> context.getBean(name, T::class.java)
}

abstract class AbstractModule : Module {

	override lateinit var context: GenericApplicationContext

	val initializers = mutableListOf<ApplicationContextInitializer<GenericApplicationContext>>()

	override fun initialize(context: GenericApplicationContext) {
		for (child in initializers) {
			child.initialize(context)
		}
	}
}