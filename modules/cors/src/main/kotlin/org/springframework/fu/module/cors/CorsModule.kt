package org.springframework.fu.module.cors

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * @author Ireneusz Kozłowski
 */
class CorsModule(
    private val defaults: Boolean = true,
    private val init: CorsModule.() -> Unit
) :
    WebFluxModule.WebServerModule, AbstractModule() {

    private val configuration = UrlBasedCorsConfigurationSource()

    override fun initialize(context: GenericApplicationContext) {
        init()
        context.registerBean("corsFilter") {
            CorsWebFilter(configuration)
        }
    }

    operator fun String.invoke(defaults: Boolean = this@CorsModule.defaults, init: CorsConfigurationDsl.() -> Unit) {
        val corsConfigurationDsl = CorsConfigurationDsl(defaults)
        corsConfigurationDsl.init()
        configuration.registerCorsConfiguration(this, corsConfigurationDsl())
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

