package org.springframework.security.config.annotation.web.reactive;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.reactive.result.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.web.reactive.result.method.annotation.CurrentSecurityContextArgumentResolver;

import java.util.function.Supplier;

/**
 * {@link ApplicationContextInitializer} adapter for {@link org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration}.
 */
public class ServerHttpSecurityInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
	private static final String BEAN_NAME_PREFIX = "org.springframework.security.config.annotation.web.reactive.HttpSecurityConfiguration.";
	static final String HTTPSECURITY_BEAN_NAME = BEAN_NAME_PREFIX + "httpSecurity";

	private final ReactiveAuthenticationManager authenticationManager;
	private final ReactiveUserDetailsService reactiveUserDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final ReactiveUserDetailsPasswordService userDetailsPasswordService;

	/**
	 * @param authenticationManager      {@link ServerHttpSecurityConfiguration}
	 * @param reactiveUserDetailsService {@link ServerHttpSecurityConfiguration}
	 * @param passwordEncoder            {@link ServerHttpSecurityConfiguration}
	 * @param userDetailsPasswordService {@link ServerHttpSecurityConfiguration}
	 */
	public ServerHttpSecurityInitializer(
			ReactiveAuthenticationManager authenticationManager,
			ReactiveUserDetailsService reactiveUserDetailsService, PasswordEncoder passwordEncoder,
			ReactiveUserDetailsPasswordService userDetailsPasswordService) {
		this.authenticationManager = authenticationManager;
		this.reactiveUserDetailsService = reactiveUserDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsPasswordService = userDetailsPasswordService;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		Supplier<ServerHttpSecurityConfiguration> configurationSupplier = new Supplier<ServerHttpSecurityConfiguration>() {
			private ServerHttpSecurityConfiguration configuration;

			@Override
			public ServerHttpSecurityConfiguration get() {
				if (configuration == null) {
					configuration = new ServerHttpSecurityConfiguration();
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
					configuration.setAdapterRegistry(context.getBean(ReactiveAdapterRegistry.class));
				}
				return configuration;
			}
		};

		context.registerBean(AuthenticationPrincipalArgumentResolver.class, () -> configurationSupplier.get().authenticationPrincipalArgumentResolver());
		context.registerBean(CurrentSecurityContextArgumentResolver.class, () -> configurationSupplier.get().reactiveCurrentSecurityContextArgumentResolver());
		context.registerBean(
				HTTPSECURITY_BEAN_NAME,
				ServerHttpSecurity.class,
				() -> configurationSupplier.get().httpSecurity(),
				bd -> {
					bd.setScope("prototype");
					bd.setAutowireCandidate(true);
				}
		);
	}
}
