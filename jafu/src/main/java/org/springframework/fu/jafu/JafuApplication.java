package org.springframework.fu.jafu;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


/**
 * Jafu application that can be run parameterized with Spring profiles and/or command line arguments.
 *
 * @see Jafu#application(Consumer)
 * @see Jafu#webApplication(Consumer)
 * @see Jafu#reactiveWebApplication(Consumer)
 */
public abstract class JafuApplication {

	private final ApplicationContextInitializer<GenericApplicationContext> initializer;

	private ApplicationContextInitializer<GenericApplicationContext> customizer;

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

			@Override
			protected ConfigurableApplicationContext createApplicationContext() {
				return createContext();
			}
		};
		if (!profiles.isEmpty()) {
			app.setAdditionalProfiles(Arrays.stream(profiles.split(",")).map(it -> it.trim()).toArray(String[]::new));
		}
		app.addInitializers(this.initializer);
		if (this.customizer != null) app.addInitializers(this.customizer);
		System.setProperty("spring.backgroundpreinitializer.ignore", "true");
		// TODO Manage lazy loading like in Kofu
		return app.run(args);
	}

	/**
	 * Customize an existing application for testing, mocking, etc.
	 */
	public JafuApplication customize(Consumer<ApplicationDsl> customizer) {
		this.customizer = new ApplicationDsl(customizer);
		return this;
	}

	protected abstract ConfigurableApplicationContext createContext();

}
