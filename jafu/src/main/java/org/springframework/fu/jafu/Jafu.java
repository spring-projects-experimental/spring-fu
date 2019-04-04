package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Provide {@link #application(Consumer)} and {@link #reactiveWebApplication(Consumer)} to declare an application.
 */
public abstract class Jafu {

	/**
	 * Declare an {@link ApplicationDsl application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration. For webflux servers,
	 * use {@link #reactiveWebApplication} instead.
	 */
	public static JafuApplication application(Consumer<ApplicationDsl> dsl) {
		return new JafuApplication(new ApplicationDsl(dsl)) {

			@Override
			protected void initializeWebApplicationContext(SpringApplication app) {
				app.setWebApplicationType(WebApplicationType.NONE);
				app.setApplicationContextClass(GenericApplicationContext.class);
			}
		};
	}

	/**
	 * Declare a {@link ApplicationDsl webflux server application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration. Requires a {@code webFlux} child element.
	 * @see #application(Consumer)
	 */
	public static JafuApplication reactiveWebApplication(Consumer<ApplicationDsl> dsl) {
		return new JafuApplication(new ApplicationDsl(dsl)) {

			@Override
			protected void initializeWebApplicationContext(SpringApplication app) {
				app.setWebApplicationType(WebApplicationType.REACTIVE);
				app.setApplicationContextClass(ReactiveWebServerApplicationContext.class);
			}
		};
	}

}
