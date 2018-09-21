/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.kofu.web

import org.springframework.web.cors.CorsConfiguration

/**
 * Kofu DSL for WebFlux CORS configuration.
 */
class CorsConfigurationDsl(defaults: Boolean = true,
						   internal val corsConfiguration: CorsConfiguration = CorsConfiguration()) {

	init {
		if (defaults) corsConfiguration.applyPermitDefaultValues()
	}

	fun allowedOrigins(allowedOrigin: String, vararg additionalAllowedOrigins: String) {
		corsConfiguration.allowedOrigins = mutableListOf(allowedOrigin).apply { addAll(additionalAllowedOrigins.toList()) }
	}

	fun allowedMethods(allowedMethod: String, vararg additionalAllowedMethods: String) {
		corsConfiguration.allowedMethods = mutableListOf(allowedMethod).apply { addAll(additionalAllowedMethods.toList()) }
	}

	fun allowedHeaders(allowedHeader: String, vararg additionalAllowedHeaders: String) {
		corsConfiguration.allowedHeaders = mutableListOf(allowedHeader).apply { addAll(additionalAllowedHeaders.toList()) }
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
