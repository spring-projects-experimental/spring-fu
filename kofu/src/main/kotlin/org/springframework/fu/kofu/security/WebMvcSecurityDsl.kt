/*
 * Copyright 2012-2020 the original author or authors.
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

package org.springframework.fu.kofu.security

import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.fu.kofu.webmvc.WebMvcServerDsl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.configuration.HttpSecurityInitializer
import org.springframework.security.config.annotation.web.configuration.ObjectPostProcessorInitializer
import org.springframework.security.config.annotation.web.configuration.WebMvcSecurityInitializer
import org.springframework.security.config.annotation.web.configuration.WebSecurityInitializer
import org.springframework.security.config.web.servlet.HttpSecurityDsl
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsPasswordService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Kofu DSL for spring-security.
 *
 * Configure spring-security.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-security`.
 *
 * @author Fred Montariol
 */
class WebMvcSecurityDsl(private val init: WebMvcSecurityDsl.() -> Unit) : AbstractDsl() {

	var authenticationManager: AuthenticationManager? = null

	var userDetailsService: UserDetailsService? = null

	var passwordEncoder: PasswordEncoder? = null

	var userDetailsPasswordService: UserDetailsPasswordService? = null

	private var httpConfiguration: HttpSecurityDsl.() -> Unit = {}

	fun http(httpConfiguration: HttpSecurityDsl.() -> Unit = {}) {
		this.httpConfiguration = httpConfiguration
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()

		ObjectPostProcessorInitializer().initialize(context)

		val securityInitializer = HttpSecurityInitializer(authenticationManager, userDetailsService, passwordEncoder,
				userDetailsPasswordService)

		securityInitializer.initialize(context)

		WebSecurityInitializer { it.invoke(httpConfiguration) }
				.initialize(context)
		WebMvcSecurityInitializer().initialize(context)
	}
}

/**
 * Configure spring-security.
 *
 * Requires `org.springframework.boot:spring-boot-starter-security` dependency.
 *
 * @see WebMvcSecurityDsl
 * @sample org.springframework.fu.kofu.samples.webMvcSecurity
 * @author Fred Montariol
 */
fun ConfigurationDsl.webMvcSecurity(dsl: WebMvcSecurityDsl.() -> Unit = {}) {
	WebMvcSecurityDsl(dsl).initialize(context)
}
