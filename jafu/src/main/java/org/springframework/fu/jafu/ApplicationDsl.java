package org.springframework.fu.jafu;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.context.MessageSourceInitializer;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Jafu DSL for application configuration.
 *
 * @author Sebastien Deleuze
 * @see #application
 */
public class ApplicationDsl extends ConfigurationDsl {

	private static class Application {}

	private final Consumer<ApplicationDsl> dsl;

	private final boolean startServer;

	ApplicationDsl(boolean startServer, Consumer<ApplicationDsl> dsl) {
		super(configurationDsl -> {});
		this.dsl = dsl;
		this.startServer = startServer;
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
		SpringApplication app = new SpringApplication(Application.class) {
			@Override
			protected void load(ApplicationContext context, Object[] sources) {
				// We don't want the annotation bean definition reader
			}
		};

		if (startServer) {
			app.setWebApplicationType(WebApplicationType.REACTIVE);
			app.setApplicationContextClass(ReactiveWebServerApplicationContext.class);
		}
		else {
			app.setWebApplicationType(WebApplicationType.NONE);
			app.setApplicationContextClass(GenericApplicationContext.class);
		}
		if (!profiles.isEmpty()) {
			app.setAdditionalProfiles(Arrays.stream(profiles.split(",")).map(it -> it.trim()).toArray(String[]::new));
		}
		app.addInitializers(this);
		System.setProperty("spring.backgroundpreinitializer.ignore", "true");
		return app.run(args);
	}

	/**
	 * Define an {@link ApplicationDsl application Jafu configuration} with {@code startServer = true}.
	 * @see #application(boolean, Consumer)
	 */
	public static ApplicationDsl application(Consumer<ApplicationDsl> dsl) {
		return application(true, dsl);
	}

	/**
	 * Define an {@link ApplicationDsl application Jafu configuration} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration.
	 *
	 * Require `org.springframework.fu:spring-fu-jafu` dependency.
	 */
	public static ApplicationDsl application(boolean startServer, Consumer<ApplicationDsl> dsl) {
		return new ApplicationDsl(startServer, dsl);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		new MessageSourceInitializer().initialize(context);
	}

}
