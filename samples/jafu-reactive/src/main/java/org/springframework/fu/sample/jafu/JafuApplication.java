package org.springframework.fu.sample.jafu;

import static org.springframework.boot.jafu.Jafu.*;

import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;

public class JafuApplication {

	public static SpringApplication app = application(applicationDsl -> {
		applicationDsl.beans(context -> {
			applicationDsl.registerBean(SampleService.class);
			applicationDsl.registerBean(SampleHandler.class);
		});
		applicationDsl.server(serverDsl -> {
			serverDsl.router(routerDsl -> {
				SampleHandler sampleHandler = serverDsl.ref(SampleHandler.class);
				routerDsl.GET("/", sampleHandler::hello);
				routerDsl.resources("/**", new ClassPathResource("static/"));
			});
		});
	});

	public static void main (String[] args) {
		app.run(args);
	}
}
