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
class CorsModule(private val init: CorsModule.() -> Unit) :
    WebFluxModule.WebServerModule, AbstractModule() {

    private val configuration = UrlBasedCorsConfigurationSource()
    override fun initialize(context: GenericApplicationContext) {
        init()
        context.registerBean("corsFilter") {
            CorsWebFilter(configuration)
        }
    }

    fun path(path: String, init: CorsConfiguration.() -> Unit) {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.init()
        configuration.registerCorsConfiguration(path, corsConfiguration)
    }
}

fun WebFluxModule.WebFluxServerModule.cors(
    init: CorsModule.() -> Unit = {}
): CorsModule {
    val corsModule = CorsModule(init)
    initializers.add(corsModule)
    return corsModule
}