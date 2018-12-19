package org.springframework.fu.jafu;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Base class for Jafu DSL.
 *
 * Make sure to invoke {@code super.initialize(context)} from {@link #initialize(GenericApplicationContext)} in
 * inherited classes to get the context initialized.
 *
 * @author Sebastien Deleuze
 */
public abstract class AbstractDsl implements ApplicationContextInitializer<GenericApplicationContext> {

	protected GenericApplicationContext context;

	/**
	 * Get a reference to the bean by type.
	 * @param beanClass type the bean must match, can be an interface or superclass
	 */
	public <T> T ref(Class<T> beanClass) {
		return this.context.getBean(beanClass);
	}

	/**
	 * Get a reference to the bean by type + name.
	 * @param beanClass type the bean must match, can be an interface or superclass
	 */
	public <T> T ref(Class<T> beanClass, String name) {
		return this.context.getBean(name, beanClass);
	}

	/**
	 * Shortcut the get the environment.
	 */
	public Environment env() {
		return context.getEnvironment();
	}

	/**
	 * Shortcut the get the active profiles.
	 */
	public List<String> profiles() {
		return Arrays.asList(context.getEnvironment().getActiveProfiles());
	}

	/**
	 * Override return type in inherited classes to return the concrete class type and make it public where you want
	 * to make it available.
	 */
	protected AbstractDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
		dsl.initialize(context);
		return this;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		this.context = context;
	}

}
