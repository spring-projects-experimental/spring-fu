package org.springframework.fu.module.cors

import org.springframework.web.cors.CorsConfiguration

class CorsConfigurationDsl(
    var defaults: Boolean = true,
    private val corsConfiguration: CorsConfiguration = CorsConfiguration()
) {
    fun allowedOrigins(vararg allowedOrigins: String) {
        corsConfiguration.allowedOrigins = allowedOrigins.toList()
    }

    fun allowedMethods(vararg allowedMethods: String) {
        corsConfiguration.allowedMethods = allowedMethods.toList()
    }

    fun allowedHeaders(vararg allowedHeaders: String) {
        corsConfiguration.allowedHeaders = allowedHeaders.toList()
    }

    fun exposedHeaders(vararg exposedHeaders: String) {
        corsConfiguration.exposedHeaders = exposedHeaders.toList()
    }

    var allowCredentials: Boolean? = null
        set(allowCredentials) {
            corsConfiguration.allowCredentials = allowCredentials
        }

    var maxAge: Long? = null
        set(maxAge) {
            corsConfiguration.maxAge
        }

    operator fun invoke(): CorsConfiguration {
        if (defaults)
            corsConfiguration.applyPermitDefaultValues()
        return corsConfiguration
    }
}

