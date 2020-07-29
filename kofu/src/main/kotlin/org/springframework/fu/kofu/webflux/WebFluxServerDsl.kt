package org.springframework.fu.kofu.webflux

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.getBeanProvider
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils.uniqueBeanName
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.reactive.*
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.io.ClassPathResource
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.fu.kofu.web.JacksonDsl
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.server.WebFilter

/**
 * Kofu DSL for WebFlux server.
 *
 * This DSL to be used in [org.springframework.fu.kofu.application] and a
 * [org.springframework.boot.WebApplicationType.REACTIVE] parameter configures a
 * [WebFlux server](https://docs.spring.io/spring/docs/current/spring-framework-reference/webflux-reactive.html#spring-webflux).
 *
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 *
 * You can chose the underlying engine via the [WebFluxServerDsl.engine] parameter.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-webflux`.
 *
 * @sample org.springframework.fu.kofu.samples.webFluxCoRouter
 * @see org.springframework.fu.kofu.application
 * @see WebFluxServerDsl.codecs
 * @see WebFluxServerDsl.cors
 * @see WebFluxServerDsl.mustache
 * @author Sebastien Deleuze
 */
open class WebFluxServerDsl(private val init: WebFluxServerDsl.() -> Unit): AbstractDsl() {

	private val serverProperties = ServerProperties()

	private val resourceProperties = ResourceProperties()

	private val webFluxProperties = WebFluxProperties()

	private var codecsConfigured: Boolean = false

	/**
	 * Define the listening port of the webFlux.
	 */
	var port: Int = 8080

	/**
	 * Define the underlying engine used.
	 *
	 * @see netty
	 * @see tomcat
	 * @see jetty
	 * @see undertow
	 */
	var engine: ConfigurableReactiveWebServerFactory? = null

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		context.registerBean(uniqueBeanName(RouterFunctionDsl::class.java.name, context)) {
			org.springframework.web.reactive.function.server.router {
				resources("/**", ClassPathResource("static/"))
			}
		}
		if (engine == null) {
			engine = netty()
		}
		engine!!.setPort(port)
		if (!codecsConfigured) {
			StringCodecInitializer(false, false).initialize(context)
			ResourceCodecInitializer(false).initialize(context)
		}
		if (context.containsBeanDefinition("webHandler")) {
			throw IllegalStateException("Only one webFlux per application is supported")
		}
		ReactiveWebServerInitializer(serverProperties, resourceProperties, webFluxProperties, engine).initialize(context)
	}

	/**
	 * Configure codecs via a [dedicated DSL][WebFluxServerCodecDsl].
	 */
	fun codecs(init: WebFluxServerCodecDsl.() -> Unit =  {}) {
		WebFluxServerCodecDsl(init).initialize(context)
		codecsConfigured = true
	}

	/**
	 * Define a request filter for this webFlux
	 */
	inline fun <reified T: WebFilter> filter() {
		context.registerBean<T>(uniqueBeanName(T::class.java.name, context))
	}

	/**
	 * Configure routes via a [dedicated DSL][RouterFunctionDsl].
	 * @sample org.springframework.fu.kofu.samples.webFluxRouter
	 */
	fun router(routes: (RouterFunctionDsl.() -> Unit)) {
		context.registerBean(uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { org.springframework.web.reactive.function.server.router(routes) }
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
	 * Configure Coroutines routes via a [dedicated DSL][CoRouterFunctionDsl].
	 * @sample org.springframework.fu.kofu.samples.webFluxCoRouter
	 */
	fun coRouter(routes: (CoRouterFunctionDsl.() -> Unit)) {
		context.registerBean(uniqueBeanName(CoRouterFunctionDsl::class.java.name, context)) { org.springframework.web.reactive.function.server.coRouter(routes) }
	}

	/**
	 * Get a reference to the bean by type or type + name with the syntax
	 * `ref<Foo>()` or `ref<Foo>("foo")`. When leveraging Kotlin type inference
	 * it could be as short as `ref()` or `ref("foo")`.
	 * TODO Update when SPR-17648 will be fixed
	 * @param name the name of the bean to retrieve
	 * @param T type the bean must match, can be an interface or superclass
	 */
	inline fun <reified T : Any> CoRouterFunctionDsl.ref(name: String? = null): T = when (name) {
		null -> context.getBean(T::class.java)
		else -> context.getBean(name, T::class.java)
	}

	/**
	 * Return an provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * TODO Update when SPR-17648 will be fixed
	 * @see org.springframework.beans.factory.BeanFactory.getBeanProvider
	 */
	inline fun <reified T : Any> CoRouterFunctionDsl.provider() : ObjectProvider<T> = context.getBeanProvider()

	/**
	 * Netty engine.
	 * TODO Use lazy val when supported by GraalVM
	 * @see engine
	 */
	fun netty() = NettyDelegate().invoke()

	/**
	 * Tomcat engine.
	 * TODO Use lazy val when supported by GraalVM
	 * @see engine
	 */
	fun tomcat() =  TomcatDelegate().invoke()

	/**
	 * Jetty engine.
	 * TODO Use lazy val when supported by GraalVM
	 * @see engine
	 */
	fun jetty() = JettyDelegate().invoke()

	/**
	 * Undertow engine.
	 * TODO Use lazy val when supported by GraalVM
	 * @see engine
	 */
	fun undertow() = UndertowDelegate().invoke()


	private class NettyDelegate: () -> ConfigurableReactiveWebServerFactory {
		override fun invoke(): ConfigurableReactiveWebServerFactory {
			return NettyReactiveWebServerFactory()
		}
	}

	private class TomcatDelegate: () -> ConfigurableReactiveWebServerFactory {
		override fun invoke(): ConfigurableReactiveWebServerFactory {
			return TomcatReactiveWebServerFactory()
		}
	}

	private class JettyDelegate: () -> ConfigurableReactiveWebServerFactory {
		override fun invoke(): ConfigurableReactiveWebServerFactory {
			return JettyReactiveWebServerFactory()
		}
	}

	private class UndertowDelegate: () -> ConfigurableReactiveWebServerFactory {
		override fun invoke(): ConfigurableReactiveWebServerFactory {
			return UndertowReactiveWebServerFactory()
		}
	}

	class WebFluxServerCodecDsl(private val init: WebFluxServerCodecDsl.() -> Unit) : AbstractDsl() {

		override fun initialize(context: GenericApplicationContext) {
			super.initialize(context)
			init()
		}

		/**
		 * Enable [org.springframework.core.codec.CharSequenceEncoder] and [org.springframework.core.codec.StringDecoder]
		 */
		fun string(textPlainOnly: Boolean = false) {
			StringCodecInitializer(false, textPlainOnly).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.ResourceHttpMessageWriter] and [org.springframework.core.codec.ResourceDecoder]
		 */
		fun resource() {
			ResourceCodecInitializer(false).initialize(context)
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON codec on WebFlux server via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-webflux`).
		 *
		 * @sample org.springframework.fu.kofu.samples.jacksonDsl
		 */
		fun jackson(dsl: JacksonDsl.() -> Unit = {}) {
			JacksonDsl(dsl).initialize(context)
			JacksonJsonCodecInitializer(false).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.protobuf.ProtobufEncoder] and [org.springframework.http.codec.protobuf.ProtobufDecoder]
		 *
		 * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
		 * supports `application/x-protobuf` and `application/octet-stream`.
		 */
		fun protobuf() {
			ProtobufCodecInitializer(false).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.FormHttpMessageWriter] and [org.springframework.http.codec.FormHttpMessageReader]
		 */
		fun form() {
			FormCodecInitializer(false).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.multipart.MultipartHttpMessageWriter] and
		 * [org.springframework.http.codec.multipart.MultipartHttpMessageReader]
		 *
		 * This codec requires Synchronoss NIO Multipart library via  the `org.synchronoss.cloud:nio-multipart-parser`
		 * dependency.
		 */
		fun multipart() {
			MultipartCodecInitializer(false).initialize(context)
		}
	}

}

/**
 * Declare a WebFlux server.
 * @see WebFluxServerDsl
 */
fun ConfigurationDsl.webFlux(dsl: WebFluxServerDsl.() -> Unit =  {}) {
	WebFluxServerDsl(dsl).initialize(context)
}
