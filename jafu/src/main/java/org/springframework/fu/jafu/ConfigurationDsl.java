package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.FunctionalConfigurationPropertiesBinder;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;


/**
 * Jafu DSL for modular configuration that can be imported in the application.
 *
 * @see ApplicationDsl#enable(Dsl)
 * @see ApplicationDsl#enable(Consumer)
 *
 * @author Sebastien Deleuze
 */
public class ConfigurationDsl extends AbstractDsl {

	private final Consumer<ConfigurationDsl> dsl;

	public ConfigurationDsl(Consumer<ConfigurationDsl> dsl) {
		super();
		this.dsl = dsl;
	}

	public ConfigurationDsl logging(Consumer<LoggingDsl> dsl) {
		new LoggingDsl(dsl);
		return this;
	}

	public <T> ConfigurationDsl properties(Class<T> clazz) {
		properties(clazz, "");
		return this;
	}

	public <T> ConfigurationDsl properties(Class<T> clazz, String prefix) {
		context.registerBean(clazz.getSimpleName() + "ConfigurationProperties", clazz, () -> new FunctionalConfigurationPropertiesBinder(context).bind(prefix, Bindable.of(clazz)).get());
		return this;
	}

	public ConfigurationDsl beans(Consumer<BeanDsl> dsl) {
		new BeanDsl(dsl).initialize(context);
		return this;
	}

	@Override
	public ConfigurationDsl enable(Dsl dsl) {
		return (ConfigurationDsl) super.enable(dsl);
	}

	public ConfigurationDsl enable(Consumer<ConfigurationDsl> dsl) {
		new ConfigurationDsl(dsl).initialize(context);
		return this;
	}

	/**
	 * Declare application event Listeners in order to run tasks when {@link ApplicationEvent}
	 * like {@link ApplicationReadyEvent} are emitted.
	 */
	public <E extends ApplicationEvent> ConfigurationDsl listener(Class<E> clazz, ApplicationListener listener) {
		context.addApplicationListener(e -> {
			// TODO Leverage SPR-16872 when it will be fixed
			if (clazz.isAssignableFrom(e.getClass())) {
				listener.onApplicationEvent(e);
			}
		});
		return this;
	}

	@Override
	public void register() {
		this.dsl.accept(this);
	}

}
