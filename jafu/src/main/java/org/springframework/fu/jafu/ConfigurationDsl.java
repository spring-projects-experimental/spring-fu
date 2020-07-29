package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.FunctionalConfigurationPropertiesBinder;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.GenericApplicationContext;


/**
 * Jafu DSL for modular configuration that can be imported in the application.
 *
 * Usage: {@code Consumer<ConfigurationDsl> conf = c -> c.beans(b -> b.bean(Foo.class));}
 *
 * @see ApplicationDsl#enable(ApplicationContextInitializer)
 * @see ApplicationDsl#enable(Consumer)
 *
 * @author Sebastien Deleuze
 */
public class ConfigurationDsl extends AbstractDsl {

	private final Consumer<ConfigurationDsl> dsl;

	ConfigurationDsl(Consumer<ConfigurationDsl> dsl) {
		super();
		this.dsl = dsl;
	}

	/**
	 * Configure global, package or class logging via a {@link LoggingDsl dedicated DSL}.
	 */
	public ConfigurationDsl logging(Consumer<LoggingDsl> dsl) {
		new LoggingDsl(dsl);
		return this;
	}

	/**
	 * Specify the class and the prefix of configuration properties, which is the same mechanism than regular Boot
	 * configuration properties without `@ConfigurationProperties` annotation.
	 * @see #configurationProperties(Class, String)
	 * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties">Type-safe Configuration Properties</a>
	 */
	public <T> ConfigurationDsl configurationProperties(Class<T> clazz) {
		configurationProperties(clazz, "");
		return this;
	}

	/**
	 * Specify the class and the optional prefix of configuration properties, which is the same mechanism than regular
	 * Boot configuration properties without `@ConfigurationProperties` annotation.
	 * @see #configurationProperties(Class)
	 * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties">Type-safe Configuration Properties</a>
	 */
	public <T> ConfigurationDsl configurationProperties(Class<T> clazz, String prefix) {
		context.registerBean(clazz.getSimpleName() + "ConfigurationProperties", clazz, () -> new FunctionalConfigurationPropertiesBinder(context).bind(prefix, Bindable.of(clazz)).get());
		return this;
	}

	/**
	 * Configure beans via a {@link BeanDefinitionDsl dedicated DSL}.
	 */
	public ConfigurationDsl beans(Consumer<BeanDefinitionDsl> dsl) {
		new BeanDefinitionDsl(dsl).initialize(context);
		return this;
	}

	/**
	 * Enable the specified functional configuration.
	 * @see #enable(Consumer)
	 */
	@Override
	public ConfigurationDsl enable(ApplicationContextInitializer<GenericApplicationContext> configuration) {
		return (ConfigurationDsl) super.enable(configuration);
	}

	/**
	 * Enable the specified functional configuration.
	 * @see #enable(ApplicationContextInitializer)
	 */
	public ConfigurationDsl enable(Consumer<ConfigurationDsl> configuration) {
		new ConfigurationDsl(configuration).initialize(context);
		return this;
	}

	/**
	 * Declare application event Listeners in order to run tasks when {@link ApplicationEvent}
	 * like {@link ApplicationReadyEvent} are emitted.
	 */
	@SuppressWarnings("unchecked")
	public <E extends ApplicationEvent> ConfigurationDsl listener(Class<E> clazz, ApplicationListener<E> listener) {
		context.addApplicationListener(e -> {
			// TODO Leverage SPR-16872 when it will be fixed
			if (clazz.isAssignableFrom(e.getClass())) {
				listener.onApplicationEvent((E)e);
			}
		});
		return this;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
	}

}
