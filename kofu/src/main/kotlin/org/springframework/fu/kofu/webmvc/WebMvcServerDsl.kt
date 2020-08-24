package org.springframework.fu.kofu.webmvc

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.getBeanProvider
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.servlet.FormConverterInitializer
import org.springframework.boot.autoconfigure.web.servlet.ResourceConverterInitializer
import org.springframework.boot.autoconfigure.web.servlet.AtomConverterInitializer
import org.springframework.boot.autoconfigure.web.servlet.JacksonJsonConverterInitializer
import org.springframework.boot.autoconfigure.web.servlet.RssConverterInitializer
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerInitializer
import org.springframework.boot.autoconfigure.web.servlet.StringConverterInitializer
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.io.ClassPathResource
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.fu.kofu.web.JacksonDsl
import org.springframework.web.servlet.function.RouterFunctionDsl

/**
 * Kofu DSL for Spring MVC server.
 *
 * This DSL to be used in [org.springframework.fu.kofu.application] and a
 * [org.springframework.boot.WebApplicationType.SERVLET] parameter configures
 * a Spring MVC server with functional routing.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-web`.
 *
 * @sample org.springframework.fu.kofu.samples.webMvcRouter
 * @see org.springframework.fu.kofu.application
 * @author Sebastien Deleuze
 */
open class WebMvcServerDsl(private val init: WebMvcServerDsl.() -> Unit): AbstractDsl() {

	private val serverProperties = ServerProperties()

	private val webMvcProperties = WebMvcProperties()

	private val resourceProperties = ResourceProperties()

	private var convertersConfigured: Boolean = false

	/**
	 * Define the listening port of Spring MVC.
	 */
	var port: Int = 8080

	/**
	 * Define the underlying engine used.
	 *
	 * @see tomcat
	 * @see jetty
	 * @see undertow
	 */
	var engine: ConfigurableServletWebServerFactory? = null

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) {
			org.springframework.web.servlet.function.router {
				resources("/**", ClassPathResource("static/"))
			}
		}
		serverProperties.servlet.isRegisterDefaultServlet = false
		serverProperties.port = port
		if (engine == null) {
			engine = tomcat()
		}
		engine!!.setPort(port)
		if (!convertersConfigured) {
			StringConverterInitializer().initialize(context)
			ResourceConverterInitializer().initialize(context)
		}
		ServletWebServerInitializer(serverProperties, webMvcProperties, resourceProperties, engine).initialize(context)
	}

	/**
	 * Configure routes via a [dedicated DSL][RouterFunctionDsl].
	 * @sample org.springframework.fu.kofu.samples.webMvcRouter
	 */
	fun router(routes: (RouterFunctionDsl.() -> Unit)) {
		context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { org.springframework.web.servlet.function.router(routes) }
	}

	/**
	 * Configure converters via a [dedicated DSL][WebMvcConverterDsl].
	 */
	fun converters(init: WebMvcConverterDsl.() -> Unit =  {}) {
		WebMvcConverterDsl(init).initialize(context)
		convertersConfigured = true
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

	/**
	 * Tomcat engine.
	 * @see engine
	 */
	fun tomcat() = TomcatDelegate().invoke()

	/**
	 * Jetty engine.
	 * @see engine
	 */
	fun jetty() = JettyDelegate().invoke()

	/**
	 * Undertow engine.
	 * @see engine
	 */
	fun undertow() = UndertowDelegate().invoke()

	private class TomcatDelegate: () -> ConfigurableServletWebServerFactory {
		override fun invoke(): ConfigurableServletWebServerFactory {
			return TomcatServletWebServerFactory()
		}
	}

	private class JettyDelegate: () -> ConfigurableServletWebServerFactory {
		override fun invoke(): ConfigurableServletWebServerFactory {
			return JettyServletWebServerFactory()
		}
	}

	private class UndertowDelegate: () -> ConfigurableServletWebServerFactory {
		override fun invoke(): ConfigurableServletWebServerFactory {
			return UndertowServletWebServerFactory()
		}
	}

	class WebMvcConverterDsl(private val init: WebMvcConverterDsl.() -> Unit) : AbstractDsl() {

		override fun initialize(context: GenericApplicationContext) {
			super.initialize(context)
			init()
		}

		/**
		 * Enable [org.springframework.http.converter.StringHttpMessageConverter]
		 */
		fun string() {
			StringConverterInitializer().initialize(context)
		}

		/**
		 * Enable [org.springframework.http.converter.ResourceHttpMessageConverter] and [org.springframework.http.converter.ResourceRegionHttpMessageConverter]
		 */
		fun resource() {
			ResourceConverterInitializer().initialize(context)
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON converter on WebMvc server via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-webflux`).
		 */
		fun jackson(dsl: JacksonDsl.() -> Unit = {}) {
			JacksonDsl(dsl).initialize(context)
			JacksonJsonConverterInitializer().initialize(context)
		}

		/**
		 * Enable [org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter]
		 */
		fun form() {
			FormConverterInitializer().initialize(context)
		}

		/**
		 * Enable [org.springframework.http.converter.feed.AtomFeedHttpMessageConverter]
		 */
		fun atom() {
			AtomConverterInitializer().initialize(context)
		}

		/**
		 * Enable [org.springframework.http.converter.feed.RssChannelHttpMessageConverter]
		 */
		fun rss() {
			RssConverterInitializer().initialize(context)
		}
	}
}

/**
 * Declare a Spring MVC server.
 * @see WebMvcServerDsl
 */
fun ConfigurationDsl.webMvc(dsl: WebMvcServerDsl.() -> Unit =  {}) {
	WebMvcServerDsl(dsl).initialize(context)
}