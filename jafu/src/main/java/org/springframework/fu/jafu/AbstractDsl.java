package org.springframework.fu.jafu;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Base class for Jafu DSL.
 *
 * @author Sebastien Deleuze
 */
public abstract class AbstractDsl implements ApplicationContextInitializer<GenericApplicationContext> {

	protected GenericApplicationContext context;

	/**
	 * Get a reference to the bean by type or type + name with the syntax
	 * @param beanClass type the bean must match, can be an interface or superclass
	 */
	public <T> T ref(Class<T> beanClass) {
		return this.context.getBean(beanClass);
	}

	public <T> T ref(Class<T> beanClass, String name) {
		return this.context.getBean(name, beanClass);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		this.context = context;
		register();
	}

	public Environment env() {
		return context.getEnvironment();
	}

	public List<String> profiles() {
		return Arrays.asList(context.getEnvironment().getActiveProfiles());
	}

	public AbstractDsl enable(ApplicationContextInitializer<GenericApplicationContext> initializer) {
		initializer.initialize(context);
		return this;
	}

	abstract public void register();
}