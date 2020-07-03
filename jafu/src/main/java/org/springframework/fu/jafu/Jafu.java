package org.springframework.fu.jafu;

import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Consumer;

/**
 * Provide entry points to declare an application.
 */
public abstract class Jafu {

	/**
	 * Declare a command-line {@link ApplicationDsl application} (no server) that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration.
	 *
	 * @see #webApplication(Consumer)
	 * @see #reactiveWebApplication(Consumer)
	 */
	public static JafuApplication application(Consumer<ApplicationDsl> dsl) {
		return new JafuApplication(new ApplicationDsl(dsl)) {
			@Override
			protected ConfigurableApplicationContext createContext() {
				return new GenericApplicationContext();
			}
		};
	}

	/**
	 * Declare a Servlet-based web {@link ApplicationDsl application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration.
	 */
	public static JafuApplication webApplication(Consumer<ApplicationDsl> dsl) {
		return new JafuApplication(new ApplicationDsl(dsl)) {
			@Override
			protected ConfigurableApplicationContext createContext() {
				return new ServletWebServerApplicationContext();
			}
		};
	}

	/**
	 * Declare a Reactive-based web {@link ApplicationDsl application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration.
	 */
	public static JafuApplication reactiveWebApplication(Consumer<ApplicationDsl> dsl) {
		return new JafuApplication(new ApplicationDsl(dsl)) {
			@Override
			protected ConfigurableApplicationContext createContext() {
				return new ReactiveWebServerApplicationContext();
			}
		};
	}

}
