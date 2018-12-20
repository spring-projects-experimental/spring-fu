package com.sample;

import static org.springframework.fu.jafu.Jafu.webApplication;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;

import org.springframework.fu.jafu.JafuApplication;

public class Application {

	public static JafuApplication app = webApplication(a -> {
		a.beans(b -> {
			b.bean(SampleHandler.class);
			b.bean(SampleService.class);
		});
		a.enable(server(s -> {
			s.port(s.profiles().contains("test") ? 8181 : 8080);
			s.router(r -> {
				SampleHandler handler = s.ref(SampleHandler.class);
				r.GET("/", handler::hello);
				r.GET("/api", handler::json);
			});
			s.codecs(c -> {
				c.string();
				c.jackson();
			});
		}));
	});

	public static void main (String[] args) {
		app.run(args);
	}
}
