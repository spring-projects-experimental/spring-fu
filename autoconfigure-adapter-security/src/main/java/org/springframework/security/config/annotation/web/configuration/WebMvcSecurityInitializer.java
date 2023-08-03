package org.springframework.security.config.annotation.web.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import java.util.function.Supplier;

/**
 * {@link ApplicationContextInitializer} adapter for {@link WebMvcSecurityConfiguration}.
 */
public class WebMvcSecurityInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		Supplier<WebMvcSecurityConfiguration> configurationSupplier = () -> {
			final WebMvcSecurityConfiguration configuration = new WebMvcSecurityConfiguration();
			configuration.setApplicationContext(context);
			return configuration;
		};

		context.registerBean(RequestDataValueProcessor.class, () -> configurationSupplier.get().requestDataValueProcessor());
	}
}
