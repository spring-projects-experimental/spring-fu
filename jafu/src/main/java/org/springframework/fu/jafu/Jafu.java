package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Provide access to Jafu DSL via static methods.
 * @see ApplicationDsl
 * @see ConfigurationDsl
 * @author Sebastien Deleuze
 */
abstract public class Jafu {

	private static class Application {}

	/**
	 * @see #application(boolean, Consumer)
	 */
	public static SpringApplication application(Consumer<ApplicationDsl> dsl) {
		return application(true, dsl);
	}

	/**
	 * Define an {@link ApplicationDsl application Jafu configuration} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration.
	 *
	 * Require `org.springframework.fu:spring-fu-jafu` dependency.
	 */
	public static SpringApplication application(boolean startServer, Consumer<ApplicationDsl> dsl) {
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
		app.addInitializers((GenericApplicationContext context) -> {
			context.registerBean("messageSource", MessageSource.class, () -> {
				ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
				messageSource.setBasename("messages");
				messageSource.setDefaultEncoding("UTF-8");
				return messageSource;
			});
		});
		app.addInitializers(new ApplicationDsl(dsl));
		System.setProperty("spring.backgroundpreinitializer.ignore", "true");
		return app;
	}

}
