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

package org.springframework.fu.kofu.web

import org.springframework.fu.kofu.AbstractDsl
import org.springframework.web.cors.CorsConfiguration

/**
 * Kofu DSL for WebFlux CORS configuration.
 *
 * @author Sebastien Deleuze
 * @author Ireneusz Kozłowski
 */
abstract class AbstractCorsDsl(private val defaults: Boolean = true) : AbstractDsl() {

	abstract fun registerCorsConfiguration(path: String, configuration: CorsConfiguration)

	/**
	 * Define a CORS configuration mapped to this path via a [dedicated DSL][CorsConfigurationDsl].
	 * @receiver the path to map the [CORS configuration][CorsConfigurationDsl]
	 * @param dsl CORS configuration DSL for the specific path
	 * @see path
	 */
	operator fun String.invoke(dsl: CorsConfigurationDsl.() -> Unit = {}) {
		path(this, dsl)
	}

	/**
	 * Define a CORS configuration mapped to this path via a [dedicated DSL][CorsConfigurationDsl].
	 * @receiver the path to map the [CORS configuration][CorsConfigurationDsl]
	 * @param path the path to map the [CORS configuration][CorsConfigurationDsl]
	 * @param dsl CORS configuration DSL for the specific path
	 * @see invoke
	 */
	fun path(path: String, dsl: CorsConfigurationDsl.() -> Unit = {}) {
		val corsConfigurationDsl = CorsConfigurationDsl(defaults)
		corsConfigurationDsl.dsl()
		registerCorsConfiguration(path, corsConfigurationDsl.corsConfiguration)
	}

	/**
	 * Kofu DSL for CORS configuration.
	 */
	class CorsConfigurationDsl(defaults: Boolean = true,
							   internal val corsConfiguration: CorsConfiguration = CorsConfiguration()) {

		init {
			if (defaults) corsConfiguration.applyPermitDefaultValues()
		}

		/**
		 * Set the origin to allow, e.g. `http://domain1.com`. The special value `*` allows all domains.
		 * By default, all origins are allowed.
		 * @see allowedOrigins
		 */
		var allowedOrigin: String
			get() = corsConfiguration.allowedOrigins!!.single()
			set(value) {
				corsConfiguration.allowedOrigins = listOf(value)
			}

		/**
		 * Set the origins to allow, e.g. `listOf("http://domain1.com", "http://domain2.com")`. The special value `*` allows all domains.
		 * By default, all origins are allowed.
		 * @see allowedOrigin
		 */
		var allowedOrigins: List<String>
			get() = corsConfiguration.allowedOrigins!!
			set(value) {
				corsConfiguration.allowedOrigins = value
			}


		/**
		 * Set the HTTP method to allow, e.g. `GET`. The special value `*` allows all methods.
		 * By default, allow "simple" methods `GET`, `HEAD` and `POST`.
		 *
		 * Note: CORS checks use values from `Forwarded` ([RFC 7239](http://tools.ietf.org/html/rfc7239),
		 * `X-Forwarded-Host`, `X-Forwarded-Port`, and `X-Forwarded-Proto` headers if present, in order
		 * to reflect the webClient-originated address.
		 */
		var allowedMethod: String
			get() = corsConfiguration.allowedMethods!!.single()
			set(value) {
				corsConfiguration.allowedMethods = listOf(value)
			}

		/**
		 * Set the HTTP methods to allow, e.g. `listOf("GET", "POST", "PUT")`. The special value `*` allows all methods.
		 * If not set, only `GET` and `HEAD` are allowed. By default, allow "simple" methods `GET`, `HEAD` and `POST`.
		 *
		 * Note: CORS checks use values from `Forwarded` ([RFC 7239](http://tools.ietf.org/html/rfc7239),
		 * `X-Forwarded-Host`, `X-Forwarded-Port`, and `X-Forwarded-Proto` headers if present, in order
		 * to reflect the webClient-originated address.
		 */
		var allowedMethods: List<String>
			get() = corsConfiguration.allowedMethods!!
			set(value) {
				corsConfiguration.allowedMethods = value
			}

		/**
		 * Set the header that a pre-flight request can list as allowed
		 * for use during an actual request, separated by commas.
		 *
		 * The special value `*` allows actual requests to send any header.
		 *
		 * A header name is not required to be listed if it is one of:
		 * `Cache-Control`, `Content-Language`, `Expires`, `Last-Modified`, or `Pragma`.
		 *
		 * By default all headers are allowed.
		 * @see allowedHeaders
		 */
		var allowedHeader: String
			get() = corsConfiguration.allowedHeaders!!.single()
			set(value) {
				corsConfiguration.allowedHeaders = listOf(value)
			}

		/**
		 * Set the list of headers that a pre-flight request can list as allowed
		 * for use during an actual request, separated by commas.
		 *
		 * The special value `*` allows actual requests to send any header.
		 *
		 * A header name is not required to be listed if it is one of:
		 * `Cache-Control`, `Content-Language`, `Expires`, `Last-Modified`, or `Pragma`.
		 *
		 * By default all headers are allowed.
		 * @see allowedHeader
		 */
		var allowedHeaders: List<String>
			get() = corsConfiguration.allowedHeaders!!
			set(value) {
				corsConfiguration.allowedHeaders = value
			}

		/**
		 * Set the response heades other than simple headers (i.e.
		 * `Cache-Control`, `Content-Language`, `Content-Type`,
		 * `Expires`, `Last-Modified`, or `Pragma`) that an actual response
		 * might have and can be exposed.
		 *
		 * Note that `*` is not a valid exposed header value.
		 *
		 * By default this is not set.
		 * @see exposedHeaders
		 */
		var exposedHeader: String?
			get() = corsConfiguration.exposedHeaders?.singleOrNull()
			set(value) {
				corsConfiguration.exposedHeaders = if (value != null) listOf(value) else null
			}

		/**
		 * Set the list of response headers other than simple headers (i.e.
		 * `Cache-Control`, `Content-Language`, `Content-Type`,
		 * `Expires`, `Last-Modified`, or `Pragma`) that an actual response
		 * might have and can be exposed.
		 *
		 * Note that `*` is not a valid exposed header value.
		 *
		 * By default this is not set.
		 * @see exposedHeader
		 */
		var exposedHeaders: List<String>?
			get() = corsConfiguration.exposedHeaders
			set(value) {
				corsConfiguration.exposedHeaders = value
			}

		/**
		 * Whether user credentials are supported.
		 * By default, user credentials are not allowed.
		 */
		var allowCredentials: Boolean?
			get() = corsConfiguration.allowCredentials
			set(allowCredentials) {
				corsConfiguration.allowCredentials = allowCredentials
			}

		/**
		 * Configure how long, in seconds, the response from a pre-flight request
		 * can be cached by clients.
		 * By default, this is set to 1800 seconds (30 minutes).
		 */
		var maxAge: Long
			get() = corsConfiguration.maxAge!!
			set(maxAge) {
				corsConfiguration.maxAge = maxAge
			}
	}
}
