package com.sample;

import org.springframework.fu.jafu.JafuApplication;

import static org.springframework.fu.jafu.Jafu.webApplication;
import static org.springframework.fu.jafu.webmvc.WebMvcServerDsl.webMvc;

public class Application {

	public static JafuApplication app = webApplication(a -> a.beans(b -> b
			.bean(SampleHandler.class)
			.bean(SampleService.class))
			.enable(webMvc(s -> s
					.port(s.profiles().contains("test") ? 8181 : 8080)
					.router(router -> {
						SampleHandler handler = s.ref(SampleHandler.class);
						router
								.GET("/", handler::hello)
								.GET("/api", handler::json);
					}).converters(c -> c
							.string()
							.jackson()))));

	public static void main (String[] args) {
		app.run(args);
	}
}
