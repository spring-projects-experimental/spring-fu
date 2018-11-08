package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.context.support.GenericApplicationContext;

/**
 * Jafu DSL for application configuration.
 *
 * @author Sebastien Deleuze
 * @see Jafu#application
 */
public class ApplicationDsl extends ConfigurationDsl {

	private final Consumer<ApplicationDsl> dsl;

	public ApplicationDsl(Consumer<ApplicationDsl> dsl) {
		super(configurationDsl -> {});
		this.dsl = dsl;
	}

	public void importConfiguration(Consumer<ConfigurationDsl> dsl) {
		this.initializers.add(new ConfigurationDsl(dsl));
	}

	@Override
	public void register(GenericApplicationContext context) {
		this.dsl.accept(this);
	}


}
