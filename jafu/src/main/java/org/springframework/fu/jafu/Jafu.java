package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.SpringFuApplication;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.reactive.context.StandardReactiveWebEnvironment;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

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
			protected SpringFuApplication getApplication() {
				return new SpringFuApplication(new GenericApplicationContext(), new StandardEnvironment());
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
			protected SpringFuApplication getApplication() {
				return new SpringFuApplication(new ReactiveWebServerApplicationContext(), new StandardReactiveWebEnvironment());
			}
		};
	}

}
