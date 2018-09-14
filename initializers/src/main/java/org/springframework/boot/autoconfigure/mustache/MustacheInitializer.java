package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * {@link ApplicationContextInitializer} adapter for {@link MustacheAutoConfiguration}.
 */
public class MustacheInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private MustacheProperties properties;

	public MustacheInitializer(MustacheProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MustacheAutoConfiguration configuration = new MustacheAutoConfiguration(this.properties, context.getEnvironment(), context);
		context.registerBean(MustacheResourceTemplateLoader.class, () -> configuration.mustacheTemplateLoader());
		context.registerBean(Mustache.Compiler.class, () -> configuration.mustacheCompiler(context.getBean(Mustache.TemplateLoader.class)));
	}
}
