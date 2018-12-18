package com.sample;

import static org.springframework.fu.jafu.JafuApplication.webApplication;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

import org.springframework.fu.jafu.JafuApplication;

public class Application {

	public static JafuApplication app = webApplication(a ->
			a.enable(server(s -> s.router(r ->
							r.GET("/", request -> ok().syncBody("Hello world!"))
					))
			));

	public static void main (String[] args) {
		app.run(args);
	}
}
