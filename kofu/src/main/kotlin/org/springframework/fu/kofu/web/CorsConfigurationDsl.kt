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

import org.springframework.web.cors.CorsConfiguration

/**
 * Kofu DSL for WebFlux CORS configuration.
 */
class CorsConfigurationDsl(defaults: Boolean = true,
						   internal val corsConfiguration: CorsConfiguration = CorsConfiguration()) {

	init {
		if (defaults) corsConfiguration.applyPermitDefaultValues()
	}

	/**
	 * Set the origins to allow, separated by commas, e.g. `https://domain1.com, https://domain2.com`. The special value `*` allows all domains.
	 * By default, all origins are allowed.
	 */
	var allowedOrigins: String = "*"
		set(value) {
			corsConfiguration.allowedOrigins = value.split(",").map { it.trim() }
		}

	/**
	 * Set the HTTP methods to allow,  separated by commas, e.g. `GET, POST, PUT`. The special value `*` allows all methods.
	 * If not set, only `GET` and `HEAD` are allowed. By default, allow "simple" methods `GET`, `HEAD` and `POST`.
	 *
	 * Note: CORS checks use values from `Forwarded` ([RFC 7239](https://tools.ietf.org/html/rfc7239),
	 * `X-Forwarded-Host`, `X-Forwarded-Port`, and `X-Forwarded-Proto` headers if present, in order
	 * to reflect the client-originated address.
	 */
	var allowedMethods: String = "GET, HEAD, POST"
		set(value) {
			corsConfiguration.allowedMethods = value.split(",").map { it.trim() }
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
	 */
	var allowedHeaders: String = "*"
		set(value) {
			corsConfiguration.allowedHeaders = value.split(",").map { it.trim() }
		}

	/**
	 * Set the list of response headers other than simple headers (i.e.
	 * `Cache-Control`, `Content-Language`, `Content-Type`,
	 * `Expires`, `Last-Modified`, or `Pragma`) that an actual response
	 * might have and can be exposed, separated by commas.
	 *
	 * Note that `*` is not a valid exposed header value.
	 *
	 * By default this is not set.
	 */
	var exposedHeaders: String? = null
		set(value) {
			corsConfiguration.exposedHeaders = (value ?: "").split(",").map { it.trim() }
		}

	/**
	 * Whether user credentials are supported.
	 *
	 * By default, user credentials are not supported.
	 */
	var allowCredentials: Boolean = false
		set(allowCredentials) {
			corsConfiguration.allowCredentials = allowCredentials
		}

	/**
	 * Configure how long, in seconds, the response from a pre-flight request
	 * can be cached by clients.
	 *
	 * By default, this is set to 1800 seconds (30 minutes).
	 */
	var maxAge: Long = 1800
		set(maxAge) {
			corsConfiguration.maxAge = maxAge
		}

}
