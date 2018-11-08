package org.springframework.fu.jafu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public abstract class AbstractDsl implements ApplicationContextInitializer<GenericApplicationContext> {

	protected final List<ApplicationContextInitializer<GenericApplicationContext>> initializers = new ArrayList<>();

	protected GenericApplicationContext context;

	public <T> T ref(Class<T> beanClass) {
		return this.context.getBean(beanClass);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		this.context = context;
		register(context);
		for (ApplicationContextInitializer<GenericApplicationContext> initializer : this.initializers) {
			initializer.initialize(context);
		}
	}

	abstract public void register(GenericApplicationContext context);
}