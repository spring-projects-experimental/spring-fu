package org.springframework.fu.jafu;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.FunctionalClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.util.ClassUtils;

/**
 * Jafu DSL for beans configuration.
 *
 * @author Sebastien Deleuze
 */
public class BeanDsl extends AbstractDsl {

	private final Consumer<BeanDsl> dsl;

	public BeanDsl(Consumer<BeanDsl> dsl) {
		this.dsl = dsl;
	}

	public final <T> BeanDsl bean(Class<T> beanClass) {
		this.context.registerBean(beanClass);
		return this;
	}

	public final <T> BeanDsl bean(String beanName, Class<T> beanClass) {
		this.context.registerBean(beanName, beanClass);
		return this;
	}

	public final <T> BeanDsl bean(Class<T> beanClass, Supplier<T> supplier) {
		this.context.registerBean(beanClass, supplier);
		return this;
	}

	public final <T> BeanDsl bean(String beanName, Class<T> beanClass, Supplier<T> supplier) {
		this.context.registerBean(beanName, beanClass, supplier);
		return this;
	}

	/**
	 * Scan beans from the provided base package.
	 *
	 * The preferred constructor (Kotlin primary constructor and standard public constructors)
	 * are evaluated for autowiring before falling back to default instantiation.
	 *
	 * TODO Support generating component indexes
	 * TODO Support exclusion
	 *
	 * @param basePackage The base package to scann
	 */
	public final BeanDsl scan(String basePackage) {
		FunctionalClassPathScanningCandidateComponentProvider componentProvider = new FunctionalClassPathScanningCandidateComponentProvider();
		for(ClassMetadata metadata : componentProvider.findCandidateComponents(basePackage)) {
			Class<?> source = ClassUtils.resolveClassName(metadata.getClassName(), null);
			String beanName = BeanDefinitionReaderUtils.uniqueBeanName(source.getName(), context);
			context.registerBean(beanName, source);
		}
		return this;
	}

	@Override
	public void register() {
		this.dsl.accept(this);
	}
}
