package org.springframework.fu

import org.springframework.context.support.GenericApplicationContext
import java.util.function.Supplier

open class ConfigurationModule<T : Any>(private val init: ConfigurationModule<T>.() -> T, private val clazz: Class<T>): AbstractModule() {

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		context.registerBean(clazz, Supplier { init() })
		super.initialize(context)
	}
}

inline fun <reified T : Any> configuration(noinline init: ConfigurationModule<*>.() -> T) = ConfigurationModule(init, T::class.java)