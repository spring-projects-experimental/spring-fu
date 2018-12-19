package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Provide {@link #application(Consumer)} and {@link #webApplication(Consumer)} to declare an application.
 */
public abstract class Jafu {

	/**
	 * Declare an {@link ApplicationDsl application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration. For web servers,
	 * use {@link #webApplication} instead.
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
	 * Declare a {@link ApplicationDsl web application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration. Requires a {@code server} child element.
	 * @see #application(Consumer)
	 */
	public static JafuApplication webApplication(Consumer<ApplicationDsl> dsl) {
		return new JafuApplication(new ApplicationDsl(dsl)) {

			@Override
			protected void initializeWebApplicationContext(SpringApplication app) {
				app.setWebApplicationType(WebApplicationType.REACTIVE);
				app.setApplicationContextClass(ReactiveWebServerApplicationContext.class);
			}
		};
	}

}
