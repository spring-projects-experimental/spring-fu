package com.sample;

import org.springframework.fu.jafu.JafuApplication;

import static org.springframework.fu.jafu.Jafu.reactiveWebApplication;
import static org.springframework.fu.jafu.webflux.WebFluxServerDsl.webFlux;

public class Application {

	public static JafuApplication app = reactiveWebApplication(a -> a
			.beans(b -> b
					.bean(SampleHandler.class)
					.bean(SampleService.class))
			.enable(webFlux(s -> s
					.port(s.profiles().contains("test") ? 8181 : 8080)
					.router(r -> {
						SampleHandler handler = s.ref(SampleHandler.class);
						r
								.GET("/", handler::hello)
								.GET("/api", handler::json);
			}).codecs(c -> c
							.string()
							.jackson()))));

	public static void main (String[] args) {
		app.run(args);
	}
}
