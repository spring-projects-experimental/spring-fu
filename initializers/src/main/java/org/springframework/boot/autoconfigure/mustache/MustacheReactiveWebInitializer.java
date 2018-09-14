package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.reactive.result.view.ViewResolver;

/**
 * {@link ApplicationContextInitializer} adapter for {@link MustacheReactiveWebConfiguration}.
 */
public class MustacheReactiveWebInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private MustacheProperties properties;

	public MustacheReactiveWebInitializer(MustacheProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MustacheReactiveWebConfiguration configuration = new MustacheReactiveWebConfiguration(properties);
		context.registerBean(ViewResolver .class, () -> configuration.mustacheViewResolver(context.getBean(Mustache.Compiler.class)));
	}
}
