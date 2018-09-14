package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * {@link ApplicationContextInitializer} adapter for {@link WebClientAutoConfiguration}.
 */
public class ReactiveWebClientInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(WebClient.Builder.class, () -> {
			// TODO Fix when SPR-17272 will be fixed and Boot updated as well
			ObjectProvider<List<WebClientCustomizer>> customizers = (ObjectProvider<List<WebClientCustomizer>>)context.getDefaultListableBeanFactory().resolveDependency(new DependencyDescriptor(MethodParameter.forParameter(WebClientAutoConfiguration.class.getConstructors()[0].getParameters()[0]), true), null);
			return new WebClientAutoConfiguration(customizers).webClientBuilder();
		});
		// TODO Can't use WebClientCodecsConfiguration because it is static protected class
	}
}
