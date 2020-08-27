package org.springframework.security.config.annotation.web.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;

/**
 * {@link ApplicationContextInitializer} adapter for {@link ObjectPostProcessorConfiguration}.
 */
public class ObjectPostProcessorInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		ObjectPostProcessorConfiguration configuration = new ObjectPostProcessorConfiguration();
		context.registerBean(ObjectPostProcessor.class, () -> configuration.objectPostProcessor(context.getBeanFactory()));
	}
}
