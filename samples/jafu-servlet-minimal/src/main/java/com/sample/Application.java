package com.sample;

import org.springframework.boot.WebApplicationType;
import org.springframework.fu.jafu.JafuApplication;

import static org.springframework.fu.jafu.Jafu.application;
import static org.springframework.fu.jafu.webflux.WebFluxServerDsl.webFlux;
import static org.springframework.fu.jafu.webmvc.WebMvcServerDsl.webMvc;

public class Application {

	public static JafuApplication app = application(WebApplicationType.SERVLET, a -> {
		a.beans(b -> {
			b.bean(SampleHandler.class);
			b.bean(SampleService.class);
		});
		a.enable(webMvc(s -> {
			s.port(s.profiles().contains("test") ? 8181 : 8080);
			s.router(r -> {
				SampleHandler handler = s.ref(SampleHandler.class);
				r.GET("/", handler::hello);
				r.GET("/api", handler::json);
			});
			s.converters(c -> {
				c.string();
				c.jackson();
			});
		}));
	});

	public static void main (String[] args) {
		app.run(args);
	}
}
