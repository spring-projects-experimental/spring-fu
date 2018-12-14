package com.sample;

import static org.springframework.fu.jafu.ApplicationDsl.*;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.fu.jafu.ApplicationDsl;

public abstract class Application {

	public static ApplicationDsl app = application(app ->
		app.enable(Configurations.dataConfig)
		   .enable(Configurations.webConfig)
		   .listener(ApplicationReadyEvent.class, e -> app.ref(UserRepository.class).init())
	);

	public static void main (String[] args) {
		if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
			System.setProperty("org.springframework.boot.logging.LoggingSystem", "org.springframework.boot.logging.java.JavaLoggingSystem");
		}
		app.run(args);
	}
}
