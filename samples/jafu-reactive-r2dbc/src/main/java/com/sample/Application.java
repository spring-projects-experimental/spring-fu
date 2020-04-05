package com.sample;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.fu.jafu.JafuApplication;

import static org.springframework.fu.jafu.Jafu.application;

public abstract class Application {

	public static JafuApplication app = application(WebApplicationType.REACTIVE, app ->
		app.enable(Configurations.dataConfig)
		   .enable(Configurations.webConfig)
		   .listener(ApplicationReadyEvent.class, e -> app.ref(UserRepository.class).init())
	);

	public static void main (String[] args) {
		app.run(args);
	}
}
