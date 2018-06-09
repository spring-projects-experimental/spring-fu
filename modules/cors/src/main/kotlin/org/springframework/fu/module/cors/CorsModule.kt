package org.springframework.fu.module.cors

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter

/**
 * @author Ireneusz Kozłowski
 */
class CorsModule(private val init: CorsModule.() -> Unit) :
    WebFluxModule.WebServerModule, AbstractModule() {

    private val configuration = CorsConfiguration()
    override fun initialize(context: GenericApplicationContext) {
        init()
        context.registerBean {
            CorsWebFilter {
                configuration
            }
        }
    }

    fun setAllowedOrigins(allowedOrigins: List<String>?) {
        configuration.allowedOrigins = allowedOrigins
    }

    fun addAllowedOrigin(origin: String) {
        configuration.addAllowedOrigin(origin)
    }

    fun setAllowedMethods(allowedMethods: List<String>?) {
        configuration.allowedMethods = allowedMethods
    }

    fun addAllowedMethod(origin: String) {
        configuration.addAllowedMethod(origin)
    }

    fun setAllowedHeaders(allowedHeaders: List<String>?) {
        configuration.allowedHeaders = allowedHeaders
    }

    fun addAllowedHeader(origin: String) {
        configuration.addAllowedHeader(origin)
    }

    fun setExposedHeaders(ExposedHeaders: List<String>?) {
        configuration.exposedHeaders = ExposedHeaders
    }

    fun addExposedHeader(origin: String) {
        configuration.addExposedHeader(origin)
    }

    fun setAllowCredentials(allowCredentials: Boolean?) {
        configuration.allowCredentials = allowCredentials
    }

    fun setMaxAge(maxAge: Long?) {
        configuration.maxAge = maxAge
    }

    fun applyPermitDefaultValues() {
        configuration.applyPermitDefaultValues()
    }
}

fun WebFluxModule.WebFluxServerModule.cors(
    init: CorsModule.() -> Unit = {}
): CorsModule {
    val corsModule = CorsModule(init)
    initializers.add(corsModule)
    return corsModule
}