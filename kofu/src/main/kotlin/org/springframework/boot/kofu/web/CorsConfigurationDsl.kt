package org.springframework.boot.kofu.web

import org.springframework.web.cors.CorsConfiguration

class CorsConfigurationDsl(
	var defaults: Boolean = true,
	val corsConfiguration: CorsConfiguration = CorsConfiguration()) {

	init {
		if (defaults) corsConfiguration.applyPermitDefaultValues()
	}

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
			corsConfiguration.maxAge = maxAge
		}

}

