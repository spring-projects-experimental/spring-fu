package org.springframework.fu.module.cors

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * @author Ireneusz Kozłowski
 */
class CorsModule(private val defaults: Boolean = true,
                 private val init: CorsModule.() -> Unit) :
    WebFluxModule.WebServerModule, AbstractModule() {

    private val configuration = UrlBasedCorsConfigurationSource()

    override fun initialize(context: GenericApplicationContext) {
        init()
        context.registerBean("corsFilter") {
            CorsWebFilter(configuration)
        }
    }

    operator fun String.invoke(init: CorsConfiguration.() -> Unit) {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.init()
        if(defaults)
            corsConfiguration.applyPermitDefaultValues()
        configuration.registerCorsConfiguration(this, corsConfiguration)
    }

    fun CorsConfiguration.allowedOrigins(vararg allowedOrigins: String) {
        this.allowedOrigins = allowedOrigins.toList()
    }

    fun CorsConfiguration.allowedMethods(vararg allowedMethods: String) {
        this.allowedMethods = allowedMethods.toList()
    }
}

fun WebFluxModule.WebFluxServerModule.cors(
    defaults: Boolean = true,
    init: CorsModule.() -> Unit = {}
): CorsModule {
    val corsModule = CorsModule(defaults, init)
    initializers.add(corsModule)
    return corsModule
}

