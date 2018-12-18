package org.springframework.fu.jafu;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


/**
 * Jafu application that can be run parameterized with Spring profiles and/or command line arguments.
 * @see #application(Consumer)
 * @see #webApplication(Consumer)
 */
public abstract class JafuApplication {

	private final ApplicationContextInitializer<GenericApplicationContext> initializer;

	protected JafuApplication(ApplicationContextInitializer<GenericApplicationContext> initializer) {
		this.initializer = initializer;
	}

	/**
	 * Run the current application
	 * @return The application context of the application
	 * @see #run(String)
	 * @see #run(String[])
	 * @see #run(String, String[])
	 */
	public ConfigurableApplicationContext run() {
		return run("", new String[0]);
	}

	/**
	 * Run the current application
	 * @param profiles {@link ApplicationContext} profiles separated by commas.
	 * @return The application context of the application
	 * @see #run()
	 * @see #run(String[])
	 * @see #run(String, String[])
	 */
	public ConfigurableApplicationContext run(String profiles) {
		return run(profiles, new String[0]);
	}

	/**
	 * Run the current application
	 * @param args the application arguments (usually passed from a Java main method)
	 * @return The application context of the application
	 * @see #run()
	 * @see #run(String)
	 * @see #run(String, String[])
	 */
	public ConfigurableApplicationContext run(String[] args) {
		return run("", args);
	}

	/**
	 * Run the current application
	 * @param profiles {@link ApplicationContext} profiles separated by commas.
	 * @param args the application arguments (usually passed from a Java main method)
	 * @return The application context of the application
	 * @see #run()
	 * @see #run(String[])
	 * @see #run(String)
	 */
	public ConfigurableApplicationContext run(String profiles, String[] args) {
		SpringApplication app = new SpringApplication(JafuApplication.class) {
			@Override
			protected void load(ApplicationContext context, Object[] sources) {
				// We don't want the annotation bean definition reader
			}
		};
		initializeWebApplicationContext(app);
		if (!profiles.isEmpty()) {
			app.setAdditionalProfiles(Arrays.stream(profiles.split(",")).map(it -> it.trim()).toArray(String[]::new));
		}
		app.addInitializers(this.initializer);
		System.setProperty("spring.backgroundpreinitializer.ignore", "true");
		return app.run(args);
	}

	protected abstract void initializeWebApplicationContext(SpringApplication app);

	/**
	 * Declare an {@link ApplicationDsl application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration. For web servers,
	 * use {@link JafuApplication#webApplication} instead.
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
