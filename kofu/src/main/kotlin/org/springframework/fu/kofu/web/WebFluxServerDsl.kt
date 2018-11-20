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
import org.springframework.context.ApplicationContextInitializer
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
 * Kofu DSL for WebFlux server configuration.
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


    override fun register(context: GenericApplicationContext) {
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
     * @see WebFluxCodecDsl.resource
     * @see WebFluxCodecDsl.string
     * @see WebFluxCodecDsl.protobuf
     * @see WebFluxCodecDsl.form
     * @see WebFluxCodecDsl.multipart
     * @see WebFluxServerCodecDsl.jackson
     */
    fun codecs(init: WebFluxServerCodecDsl.() -> Unit =  {}) {
        addInitializer(WebFluxServerCodecDsl(init))
        codecsConfigured = true
    }

    /**
     * Define a request filter for this server
     */
    fun filter(filter: WebFilter) {
        addInitializer(ApplicationContextInitializer { it.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { filter } })
    }

    /**
     * Configure routes via a [dedicated DSL][RouterFunctionDsl].
     * @sample org.springframework.fu.kofu.samples.router
     */
    fun router(routes: (RouterFunctionDsl.() -> Unit)) {
        addInitializer(ApplicationContextInitializer { it.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { RouterFunctionDsl(routes).invoke() } })
    }

    /**
     * Configure Coroutines routes via a [dedicated DSL][CoRouterFunctionDsl].
     * @sample org.springframework.fu.kofu.samples.coRouter
     */
    fun coRouter(routes: (CoRouterFunctionDsl.() -> Unit)) {
        addInitializer(ApplicationContextInitializer { it.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(CoRouterFunctionDsl::class.java.name, context)) { CoRouterFunctionDsl(routes).invoke() } })
    }

    /**
     * Import routes written using a [dedicated DSL][RouterFunctionDsl].
     * @sample org.springframework.fu.kofu.samples.importRouter
     * @sample org.springframework.fu.kofu.samples.importCoRouter
     */
    fun import(router: RouterFunction<ServerResponse>) {
        addInitializer(ApplicationContextInitializer {
            context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { router }
        })
    }
    fun import(f: Function0<RouterFunction<ServerResponse>>) {
        addInitializer(ApplicationContextInitializer {
            context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke() }
        })
    }
    inline fun <reified A: Any> import(crossinline f: Function1<A, RouterFunction<ServerResponse>>) {
        addInitializer(ApplicationContextInitializer {
            context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean()) }
        })
    }
    inline fun <reified A: Any, reified B: Any> import(crossinline f: Function2<A, B, RouterFunction<ServerResponse>>) {
        addInitializer(ApplicationContextInitializer {
            context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean()) }
        })
    }
    inline fun <reified A: Any, reified B: Any, reified C: Any> import(crossinline f: Function3<A, B, C, RouterFunction<ServerResponse>>) {
        addInitializer(ApplicationContextInitializer {
            context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean()) }
        })
    }
    inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any> import(crossinline f: Function4<A, B, C, D, RouterFunction<ServerResponse>>) {
        addInitializer(ApplicationContextInitializer {
            context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean(), context.getBean()) }
        })
    }
    inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any> import(crossinline f: Function5<A, B, C, D, E, RouterFunction<ServerResponse>>) {
        addInitializer(ApplicationContextInitializer {
            context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunctionDsl::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean(), context.getBean(), context.getBean()) }
        })
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

    class WebFluxServerCodecDsl(private val init: WebFluxServerCodecDsl.() -> Unit) : WebFluxCodecDsl() {

        override fun register(context: GenericApplicationContext) {
            init()
        }

        override fun string() {
            addInitializer(StringCodecInitializer(false))
        }

        override fun resource() {
            addInitializer(ResourceCodecInitializer(false))
        }

        override fun protobuf() {
            addInitializer(ProtobufCodecInitializer(false))
        }

        override fun form() {
            addInitializer(FormCodecInitializer(false))
        }

        override fun multipart() {
            addInitializer(MultipartCodecInitializer(false))
        }
    }

}

/**
 * Configure a WebFlux server via a via a [dedicated DSL][WebFluxServerDsl].
 *
 * This DSL configures [WebFlux server](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux).
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 * [org.springframework.fu.kofu.ApplicationDsl.startServer] needs to be set to `true` (it is by default).
 *
 * You can chose the underlying engine via the [WebFluxServerDsl.engine] parameter.
 *
 * Require `org.springframework.boot:spring-boot-starter-webflux` dependency.
 *
 * @sample org.springframework.fu.kofu.samples.router
 * @see WebFluxServerDsl.import
 * @see WebFluxServerDsl.codecs
 * @see WebFluxServerDsl.cors
 * @see WebFluxServerDsl.mustache
 */
fun ConfigurationDsl.server(dsl: WebFluxServerDsl.() -> Unit =  {}) {
    addInitializer(WebFluxServerDsl(dsl))
}
