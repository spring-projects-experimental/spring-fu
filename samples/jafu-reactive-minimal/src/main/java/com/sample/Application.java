package com.sample;

import static org.springframework.fu.jafu.ApplicationDsl.*;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;

import org.springframework.core.io.ClassPathResource;
import org.springframework.fu.jafu.ApplicationDsl;

public class Application {

	public static ApplicationDsl app = application(app ->
		app.beans(beans -> beans
			.bean(SampleService.class)
			.bean(SampleHandler.class))
			.enable(server(s -> s.router(router -> {
				var sampleHandler = app.ref(SampleHandler.class);
				router.GET("/", sampleHandler::hello)
					.resources("/**", new ClassPathResource("static/"));
			})
		))
	);

	public static void main (String[] args) {
		app.run(args);
	}
}
