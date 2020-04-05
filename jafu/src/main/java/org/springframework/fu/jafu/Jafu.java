package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Provide {@link #application(WebApplicationType, Consumer)} entry pint to declare an application.
 */
public abstract class Jafu {

	/**
	 * Declare an {@link ApplicationDsl application} that allows to configure a Spring Boot
	 * application using Jafu DSL and functional bean registration.
	 */
	public static JafuApplication application(WebApplicationType webApplicationType, Consumer<ApplicationDsl> dsl) {
		return new JafuApplication(new ApplicationDsl(dsl)) {

			@Override
			protected void initializeWebApplicationContext(SpringApplication app) {
				app.setWebApplicationType(webApplicationType);
				switch (webApplicationType) {
					case NONE:
						app.setApplicationContextClass(GenericApplicationContext.class);
						break;
					case REACTIVE:
						app.setApplicationContextClass(ReactiveWebServerApplicationContext.class);
						break;
					case SERVLET:
						app.setApplicationContextClass(ServletWebServerApplicationContext.class);
				}
			}
		};
	}

}
