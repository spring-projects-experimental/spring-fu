package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.context.MessageSourceInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Jafu DSL for application configuration.
 *
 * @author Sebastien Deleuze
 * @see JafuApplication#application
 * @see JafuApplication#webApplication
 */
public class ApplicationDsl extends ConfigurationDsl {

	private final Consumer<ApplicationDsl> dsl;

	ApplicationDsl(Consumer<ApplicationDsl> dsl) {
		super(configurationDsl -> {});
		this.dsl = dsl;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		new MessageSourceInitializer().initialize(context);
	}

}
