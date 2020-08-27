package org.springframework.security.config.annotation.web.reactive;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.web.reactive.result.view.AbstractView;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link ApplicationContextInitializer} adapter for {@link WebFluxSecurityConfiguration}.
 */
public class WebFluxSecurityInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
	private static final String BEAN_NAME_PREFIX = "org.springframework.security.config.annotation.web.reactive.WebFluxSecurityConfiguration.";
	private static final String SPRING_SECURITY_WEBFILTERCHAINFILTER_BEAN_NAME = BEAN_NAME_PREFIX + "WebFilterChainFilter";

	private final Function<ServerHttpSecurity, SecurityWebFilterChain> httpSecurityDsl;

	public WebFluxSecurityInitializer(Function<ServerHttpSecurity, SecurityWebFilterChain> httpSecurityDsl) {
		this.httpSecurityDsl = httpSecurityDsl;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		Supplier<WebFluxSecurityConfiguration> configurationSupplier = new Supplier<WebFluxSecurityConfiguration>() {
			private WebFluxSecurityConfiguration configuration;

			@Override
			public WebFluxSecurityConfiguration get() {
				if (configuration == null) {
					configuration = new WebFluxSecurityConfiguration();
					configuration.context = context;

					if (httpSecurityDsl != null) {
						ServerHttpSecurity httpSecurity = context.getBean(
								ServerHttpSecurityInitializer.HTTPSECURITY_BEAN_NAME, ServerHttpSecurity.class);
						configuration.setSecurityWebFilterChains(
								Collections.singletonList(httpSecurityDsl.apply(httpSecurity)));
					}
				}
				return configuration;
			}
		};

		context.registerBean(
				SPRING_SECURITY_WEBFILTERCHAINFILTER_BEAN_NAME,
				WebFilterChainProxy.class,
				() -> configurationSupplier.get().springSecurityWebFilterChainFilter()
		);
		context.registerBean(
				AbstractView.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME,
				CsrfRequestDataValueProcessor.class,
				() -> configurationSupplier.get().requestDataValueProcessor()
		);
	}
}
