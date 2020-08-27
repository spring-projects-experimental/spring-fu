package org.springframework.security.config.annotation.web.reactive;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.web.reactive.result.view.AbstractView;

import java.util.Collections;

/**
 * {@link ApplicationContextInitializer} adapter for {@link WebFluxSecurityConfiguration}.
 */
public class WebFluxSecurityInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
	private static final String BEAN_NAME_PREFIX = "org.springframework.security.config.annotation.web.reactive.WebFluxSecurityConfiguration.";
	private static final String SPRING_SECURITY_WEBFILTERCHAINFILTER_BEAN_NAME = BEAN_NAME_PREFIX + "WebFilterChainFilter";

	private final SecurityWebFilterChain webFilterChain;

	public WebFluxSecurityInitializer(SecurityWebFilterChain webFilterChain) {
		this.webFilterChain = webFilterChain;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		WebFluxSecurityConfiguration configuration = new WebFluxSecurityConfiguration();
		configuration.context = context;

		if (webFilterChain != null) {
			configuration.setSecurityWebFilterChains(Collections.singletonList(webFilterChain));
		}

		context.registerBean(
				SPRING_SECURITY_WEBFILTERCHAINFILTER_BEAN_NAME,
				WebFilterChainProxy.class,
				configuration::springSecurityWebFilterChainFilter
		);
		context.registerBean(
				AbstractView.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME,
				CsrfRequestDataValueProcessor.class,
				configuration::requestDataValueProcessor
		);
	}
}
