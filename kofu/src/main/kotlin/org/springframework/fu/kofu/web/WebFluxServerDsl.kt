package org.springframework.fu.kofu.web

import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
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
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.web.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.WebFilter

/**
 * Kofu DSL for WebFlux server.
 *
 * This DSL to be used in [org.springframework.fu.kofu.webApplication] configures a
 * [WebFlux server](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux).
 *
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 *
 * You can chose the underlying engine via the [WebFluxServerDsl.engine] parameter.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-webflux`.
 *
 * @sample org.springframework.fu.kofu.samples.router
 * @see org.springframework.fu.kofu.webApplication
 * @see WebFluxServerDsl.include
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
     * Define the listening port of the server.
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
        if (engine == null) {
            engine = netty()
        }
        engine!!.setPort(port)
        if (!codecsConfigured) {
            StringCodecInitializer(false).initialize(context)
            ResourceCodecInitializer(false).initialize(context)
        }
        if (context.containsBeanDefinition("webHandler")) {
            throw IllegalStateException("Only one server per application is supported")
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
     * Define a request filter for this server
     */
    inline fun <reified T: WebFilter> filter() {
        context.registerBean<T>(BeanDefinitionReaderUtils.uniqueBeanName(T::class.java.name, context))
    }

    /**
     * Configure routes via a [dedicated DSL][RouterFunctionDsl].
     * @sample org.springframework.fu.kofu.samples.router
     */
    fun router(routes: (RouterFunctionDsl.() -> Unit)) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { RouterFunctionDsl(routes).invoke() }
    }

    /**
     * Configure Coroutines routes via a [dedicated DSL][CoRouterFunctionDsl].
     * @sample org.springframework.fu.kofu.samples.coRouter
     */
    fun coRouter(routes: (CoRouterFunctionDsl.() -> Unit)) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(CoRouterFunctionDsl::class.java.name, context)) { CoRouterFunctionDsl(routes).invoke() }
    }

    /**
     * Include routes written using a [dedicated DSL][RouterFunctionDsl].
     * @sample org.springframework.fu.kofu.samples.includeRouter
     * @sample org.springframework.fu.kofu.samples.includeCoRouter
     */
    fun include(router: RouterFunction<ServerResponse>) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { router }
    }

    fun include(f: Function0<RouterFunction<ServerResponse>>) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke() }
    }

    inline fun <reified A: Any> include(crossinline f: Function1<A, RouterFunction<ServerResponse>>) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean()) }
    }
    inline fun <reified A: Any, reified B: Any> include(crossinline f: Function2<A, B, RouterFunction<ServerResponse>>) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean()) }
    }
    inline fun <reified A: Any, reified B: Any, reified C: Any> include(crossinline f: Function3<A, B, C, RouterFunction<ServerResponse>>) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean()) }
    }
    inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any> include(crossinline f: Function4<A, B, C, D, RouterFunction<ServerResponse>>) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean(), context.getBean()) }
    }
    inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any> include(crossinline f: Function5<A, B, C, D, E, RouterFunction<ServerResponse>>) {
        context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean(), context.getBean(), context.getBean()) }
    }

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
        fun string() {
            StringCodecInitializer(false).initialize(context)
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
            JacksonDsl(false, dsl).initialize(context)
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
fun ConfigurationDsl.server(dsl: WebFluxServerDsl.() -> Unit =  {}) {
    WebFluxServerDsl(dsl).initialize(context)
}
