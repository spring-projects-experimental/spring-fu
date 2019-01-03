package org.springframework.fu.jafu;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.support.GenericApplicationContext;


/**
 * Jafu DSL for beans configuration.
 *
 * @see ConfigurationDsl#beans(Consumer)
 * @author Sebastien Deleuze
 */
public class BeanDefinitionDsl extends AbstractDsl {

	private final Consumer<BeanDefinitionDsl> dsl;

	BeanDefinitionDsl(Consumer<BeanDefinitionDsl> dsl) {
		this.dsl = dsl;
	}

	/**
	 * Declare a bean definition from the given bean class.
	 */
	public <T> BeanDefinitionDsl bean(Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
		String beanName = BeanDefinitionReaderUtils.uniqueBeanName(beanClass.getName(), context);
		this.context.registerBean(beanName, beanClass, customizers);
		return this;
	}

	/**
	 * Declare a bean definition from the given bean name and class.
	 */
	public <T> BeanDefinitionDsl bean(String beanName, Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
		this.context.registerBean(beanName, beanClass);
		return this;
	}

	/**
	 * Declare a bean definition from the given bean class and supplier.
	 */
	public <T> BeanDefinitionDsl bean(Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
		String beanName = BeanDefinitionReaderUtils.uniqueBeanName(beanClass.getName(), context);
		this.context.registerBean(beanName, beanClass, supplier, customizers);
		return this;
	}

	/**
	 * Declare a bean definition from the given bean name, class and supplier.
	 */
	public <T> BeanDefinitionDsl bean(String beanName, Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
		this.context.registerBean(beanName, beanClass, supplier, customizers);
		return this;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
	}
}
