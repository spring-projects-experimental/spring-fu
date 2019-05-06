package org.springframework.fu.kofu.webmvc

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.getBeanProvider
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.boot.autoconfigure.http.HttpProperties
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerInitializer
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.fu.kofu.webflux.WebFluxServerDsl
import org.springframework.web.servlet.function.RouterFunctionDsl

/**
 * Kofu DSL for Spring MVC server.
 *
 * This DSL to be used in [org.springframework.fu.kofu.application] configures a Spring MVC server
 * with functional routing.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-web`.
 *
 * @sample org.springframework.fu.kofu.samples.router
 * @see org.springframework.fu.kofu.application
 * @author Sebastien Deleuze
 */
open class WebMvcServerDsl(private val init: WebMvcServerDsl.() -> Unit): AbstractDsl() {

	private val serverProperties = ServerProperties()

	private val httpProperties = HttpProperties()

	private val webMvcProperties = WebMvcProperties()

	private val resourceProperties = ResourceProperties()

	/**
	 * Define the listening port of Spring MVC.
	 */
	var port: Int = 8080

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		serverProperties.port = port
		ServletWebServerInitializer(serverProperties, httpProperties, webMvcProperties, resourceProperties).initialize(context)
	}

	/**
	 * Configure routes via a [dedicated DSL][RouterFunctionDsl].
	 * @sample org.springframework.fu.kofu.samples.router
	 */
	fun router(routes: (RouterFunctionDsl.() -> Unit)) {
		context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { org.springframework.web.servlet.function.router(routes) }
	}

	/**
	 * Get a reference to the bean by type or type + name with the syntax
	 * `ref<Foo>()` or `ref<Foo>("foo")`. When leveraging Kotlin type inference
	 * it could be as short as `ref()` or `ref("foo")`.
	 * TODO Update when SPR-17648 will be fixed
	 * @param name the name of the bean to retrieve
	 * @param T type the bean must match, can be an interface or superclass
	 */
	inline fun <reified T : Any> RouterFunctionDsl.ref(name: String? = null): T = when (name) {
		null -> context.getBean(T::class.java)
		else -> context.getBean(name, T::class.java)
	}

	/**
	 * Return an provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * TODO Update when SPR-17648 will be fixed
	 * @see org.springframework.beans.factory.BeanFactory.getBeanProvider
	 */
	inline fun <reified T : Any> RouterFunctionDsl.provider() : ObjectProvider<T> = context.getBeanProvider()
}

/**
 * Declare a Spring MVC server.
 * @see WebFluxServerDsl
 */
fun ConfigurationDsl.webMvc(dsl: WebMvcServerDsl.() -> Unit =  {}) {
	WebMvcServerDsl(dsl).initialize(context)
}