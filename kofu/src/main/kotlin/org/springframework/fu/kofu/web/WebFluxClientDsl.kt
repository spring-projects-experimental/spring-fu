package org.springframework.fu.kofu.web

import org.springframework.boot.autoconfigure.web.reactive.*
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientBuilderInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.web.reactive.function.client.WebClient

/**
 * Kofu DSL for WebFlux client configuration.
 * @author Sebastien Deleuze
 */
class WebFluxClientDsl(private val init: WebFluxClientDsl.() -> Unit) : AbstractDsl() {

    private var codecsConfigured: Boolean = false

    /**
     * Configure a base URL for requests performed through the client.
     */
    var baseUrl: String? = null

    override fun register(context: GenericApplicationContext) {
        init()
        if (!codecsConfigured) {
            StringCodecInitializer(true).initialize(context)
            ResourceCodecInitializer(true).initialize(context)
        }
        ReactiveWebClientBuilderInitializer(baseUrl).initialize(context)
    }

    /**
     * Configure codecs via a [dedicated DSL][WebFluxClientCodecDsl].
     * @see WebFluxCodecDsl.resource
     * @see WebFluxCodecDsl.string
     * @see WebFluxCodecDsl.protobuf
     * @see WebFluxCodecDsl.form
     * @see WebFluxCodecDsl.multipart
     * @see WebFluxClientCodecDsl.jackson
     */
    fun codecs(dsl: WebFluxClientCodecDsl.() -> Unit =  {}) {
        addInitializer(WebFluxClientCodecDsl(dsl))
        codecsConfigured = true
    }

    class WebFluxClientCodecDsl(private val init: WebFluxClientCodecDsl.() -> Unit) : WebFluxCodecDsl() {

        override fun register(context: GenericApplicationContext) {
            init()
        }

        override fun string() {
            addInitializer(StringCodecInitializer(true))
        }

        override fun resource() {
            addInitializer(ResourceCodecInitializer(true))
        }

        override fun protobuf() {
            addInitializer(ProtobufCodecInitializer(true))
        }

        override fun form() {
            addInitializer(FormCodecInitializer(true))
        }

        override fun multipart() {
            addInitializer(MultipartCodecInitializer(true))
        }
    }
}

/**
 * Configure a [WebFlux client](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-client) builder ([WebClient.builder]) via a [dedicated DSL][WebFluxClientBuilderDsl].
 *
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, no one is configured by default.
 *
 * Require `org.springframework.boot:spring-boot-starter-webflux` dependency.
 *
 * @param dsl The [WebFlux client](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-client) builder ([WebClient.builder]) DSL
 * @sample org.springframework.fu.kofu.samples.clientDsl
 * @see WebFluxClientDsl.codecs
 */
fun ConfigurationDsl.client(dsl: WebFluxClientDsl.() -> Unit =  {}) {
    addInitializer(WebFluxClientDsl(dsl))
}
