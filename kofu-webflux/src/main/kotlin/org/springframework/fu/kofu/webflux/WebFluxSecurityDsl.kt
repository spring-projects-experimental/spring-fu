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

package org.springframework.fu.kofu.webflux

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.getBeanProvider
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.ServerHttpSecurityInitializer
import org.springframework.security.config.annotation.web.reactive.WebFluxSecurityInitializer
import org.springframework.security.config.web.server.ServerHttpSecurityDsl
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.context.ServerSecurityContextRepository

/**
 * Kofu DSL for spring-security.
 *
 * Configure spring-security.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-security`.
 *
 * @author Jonas Bark, Ivan Skachkov, Fred Montariol
 */
class WebFluxSecurityDsl(private val init: WebFluxSecurityDsl.() -> Unit) : AbstractDsl() {

	var authenticationManager: ReactiveAuthenticationManager? = null

	var userDetailsService: ReactiveUserDetailsService? = null

	var passwordEncoder: PasswordEncoder? = null

	var userDetailsPasswordService: ReactiveUserDetailsPasswordService? = null

	var securityContextRepository: ServerSecurityContextRepository? = null

	private var httpConfiguration: ServerHttpSecurityDsl.() -> Unit = {}

	fun http(httpConfiguration: ServerHttpSecurityDsl.() -> Unit = {}) {
		this.httpConfiguration = httpConfiguration
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()

		val securityInitializer = ServerHttpSecurityInitializer(
				authenticationManager,
				userDetailsService,
				passwordEncoder,
				userDetailsPasswordService
		)
		securityInitializer.initialize(context)
		WebFluxSecurityInitializer {
			if (securityContextRepository != null) {
				it.securityContextRepository(securityContextRepository)
			} else {
				it
			}
					.invoke(httpConfiguration)
		}
				.initialize(context)
	}
}

/**
 * Configure spring-security.
 *
 * Requires `org.springframework.boot:spring-boot-starter-security` dependency.
 *
 * @see WebFluxSecurityDsl
 * @sample org.springframework.fu.kofu.samples.webFluxSecurity
 * @author Jonas Bark
 */
fun WebFluxServerDsl.security(dsl: WebFluxSecurityDsl.() -> Unit = {}) {
	WebFluxSecurityDsl(dsl).initialize(context)
}

/**
 * Get a reference to the bean by type or type + name with the syntax
 * `ref<Foo>()` or `ref<Foo>("foo")`. When leveraging Kotlin type inference
 * it could be as short as `ref()` or `ref("foo")`.
 * TODO Update when SPR-17648 will be fixed
 * @param name the name of the bean to retrieve
 * @param T type the bean must match, can be an interface or superclass
 */
inline fun <reified T : Any> WebFluxSecurityDsl.ref(name: String? = null): T = when (name) {
	null -> context.getBean(T::class.java)
	else -> context.getBean(name, T::class.java)
}

/**
 * Return an provider for the specified bean, allowing for lazy on-demand retrieval
 * of instances, including availability and uniqueness options.
 * TODO Update when SPR-17648 will be fixed
 * @see org.springframework.beans.factory.BeanFactory.getBeanProvider
 */
inline fun <reified T : Any> WebFluxSecurityDsl.provider() : ObjectProvider<T> = context.getBeanProvider()
