package org.springframework.boot.kofu

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.env.Environment

@DslMarker
annotation class DslMarker

@DslMarker
interface Dsl : ApplicationContextInitializer<GenericApplicationContext> {

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
inline fun <reified T : Any> Dsl.ref(name: String? = null): T = when (name) {
	null -> context.getBean(T::class.java)
	else -> context.getBean(name, T::class.java)
}

abstract class AbstractDsl : Dsl {

	override lateinit var context: GenericApplicationContext

	val initializers = mutableListOf<ApplicationContextInitializer<GenericApplicationContext>>()

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		register(context)
		for (child in initializers) {
			child.initialize(context)
		}
	}

	abstract fun register(context: GenericApplicationContext)

}