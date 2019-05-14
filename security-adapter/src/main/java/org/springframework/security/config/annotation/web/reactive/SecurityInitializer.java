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

package org.springframework.security.config.annotation.web.reactive;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.reactive.result.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.web.reactive.result.method.annotation.CurrentSecurityContextArgumentResolver;

/**
 * {@link ApplicationContextInitializer} adapter for {@link org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration}.
 */
public class SecurityInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private ReactiveAuthenticationManager authenticationManager;
	private ReactiveUserDetailsService reactiveUserDetailsService;
	private PasswordEncoder passwordEncoder;
	private ReactiveUserDetailsPasswordService userDetailsPasswordService;
	private ServerHttpSecurity httpSecurity;

	/**
	 * @see ServerHttpSecurityConfiguration
	 * @param authenticationManager {@link ServerHttpSecurityConfiguration}
	 * @param reactiveUserDetailsService {@link ServerHttpSecurityConfiguration}
	 * @param passwordEncoder {@link ServerHttpSecurityConfiguration}
	 * @param userDetailsPasswordService {@link ServerHttpSecurityConfiguration}
	 * @param httpSecurity {@link ServerHttpSecurityConfiguration}
	 */
	public SecurityInitializer(ReactiveAuthenticationManager authenticationManager, ReactiveUserDetailsService reactiveUserDetailsService, PasswordEncoder passwordEncoder, ReactiveUserDetailsPasswordService userDetailsPasswordService, ServerHttpSecurity httpSecurity) {
		this.authenticationManager = authenticationManager;
		this.reactiveUserDetailsService = reactiveUserDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsPasswordService = userDetailsPasswordService;
		this.httpSecurity = httpSecurity;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		ServerHttpSecurityConfiguration configuration = new ServerHttpSecurityConfiguration();

		if (authenticationManager != null) {
			configuration.setAuthenticationManager(authenticationManager);
		}
		if (reactiveUserDetailsService != null) {
			configuration.setReactiveUserDetailsService(reactiveUserDetailsService);
		}
		if (passwordEncoder != null) {
			configuration.setPasswordEncoder(passwordEncoder);
		}
		if (userDetailsPasswordService != null) {
			configuration.setUserDetailsPasswordService(userDetailsPasswordService);
		}

		context.registerBean(AuthenticationPrincipalArgumentResolver.class, configuration::authenticationPrincipalArgumentResolver);
		context.registerBean(CurrentSecurityContextArgumentResolver.class, configuration::reactiveCurrentSecurityContextArgumentResolver);
		context.registerBean(
				"org.springframework.security.config.annotation.web.reactive.HttpSecurityConfiguration.httpSecurity",
				ServerHttpSecurity.class,
				() -> httpSecurity != null ? httpSecurity : configuration.httpSecurity(),
				(BeanDefinitionCustomizer) bd -> {
					bd.setScope("prototype");
					bd.setAutowireCandidate(true);
				}
		);
	}
}
