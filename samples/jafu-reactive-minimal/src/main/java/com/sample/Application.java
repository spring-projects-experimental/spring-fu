package com.sample;

import static org.springframework.fu.jafu.ApplicationDsl.*;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

import org.springframework.fu.jafu.ApplicationDsl;

public class Application {

	public static ApplicationDsl app = application(a ->
			a.enable(server(s -> s.router(r ->
							r.GET("/", request -> ok().syncBody("Hello world!"))
					))
			));

	public static void main (String[] args) {
		app.run(args);
	}
}
