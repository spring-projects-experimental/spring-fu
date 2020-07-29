package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.context.MessageSourceInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Jafu top level DSL for application which allows to configure a Spring Boot
 * application using Jafu and functional bean registration.
 *
 * @author Sebastien Deleuze
 * @see org.springframework.fu.jafu.Jafu#application
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
