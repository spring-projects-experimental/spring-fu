package com.sample;

import static org.springframework.fu.jafu.ApplicationDsl.*;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.fu.jafu.ApplicationDsl;

public abstract class Application {

	public static ApplicationDsl app = application(app ->
		app.importConfiguration(Configurations.dataConfig)
		   .importConfiguration(Configurations.webConfig)
		   .properties(SampleProperties.class, "sample")
		   .listener(ApplicationReadyEvent.class, e -> app.ref(UserRepository.class).init())
	);

	public static void main (String[] args) {
		app.run(args);
	}
}
