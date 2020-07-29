package org.springframework.fu.kofu

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.getBeanProvider
import org.springframework.context.support.GenericApplicationContext

/**
 * Allow to retrieve beans with a [org.springframework.context.support.BeanDefinitionDsl] like API.
 * TODO Update when SPR-17648 will be fixed
 * @author Sebastien Deleuze
 */
open class BeanDefinitionContext(@PublishedApi internal val context: GenericApplicationContext) {

	/**
	 * Get a reference to the bean by type or type + name with the syntax
	 * `ref<Foo>()` or `ref<Foo>("foo")`. When leveraging Kotlin type inference
	 * it could be as short as `ref()` or `ref("foo")`.
	 * @param name the name of the bean to retrieve
	 * @param T type the bean must match, can be an interface or superclass
	 */
	inline fun <reified T : Any> ref(name: String? = null): T = when (name) {
		null -> context.getBean(T::class.java)
		else -> context.getBean(name, T::class.java)
	}

	/**
	 * Return an provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * TODO Maybe provide direct access to [ObjectProvider] methods, expose [Sequence] instead of [java.util.stream.Stream], etc.
	 * @see org.springframework.beans.factory.BeanFactory.getBeanProvider
	 */
	inline fun <reified T : Any> provider() : ObjectProvider<T> = context.getBeanProvider()
}