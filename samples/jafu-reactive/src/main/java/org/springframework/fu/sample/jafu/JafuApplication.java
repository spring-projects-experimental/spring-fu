package org.springframework.fu.sample.jafu;

import static org.springframework.boot.jafu.Jafu.*;

import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;

public class JafuApplication {

	public static SpringApplication app = application(app -> {
		app.beans(beans -> {
			beans.registerBean(SampleService.class);
			beans.registerBean(SampleHandler.class);
		});
		app.server(server -> server.router(router -> {
			SampleHandler sampleHandler = app.ref(SampleHandler.class);
			router.GET("/", sampleHandler::hello);
			router.resources("/**", new ClassPathResource("static/"));
		}));
	});

	public static void main (String[] args) {
		app.run(args);
	}
}
