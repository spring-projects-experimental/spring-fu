package org.springframework.fu.jafu.web;

import static org.springframework.fu.jafu.ApplicationDsl.application;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.fu.jafu.ApplicationDsl;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

public class MustacheDslTests {

	@Test
	void createAndRequestAMustacheView() {
		RouterFunction<ServerResponse> router = RouterFunctions
				.route()
				.GET("/view", request -> ok().render("template", Collections.singletonMap("name", "world")))
				.build();
		ApplicationDsl app = application(a -> a.server(s -> s.mustache().importRouter(router)));

		ConfigurableApplicationContext context = app.run();
		WebTestClient client = WebTestClient.bindToServer().baseUrl("http://0.0.0.0:8080").build();
		client.get().uri("/view").exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(String.class)
				.isEqualTo("Hello world!");
		context.close();
	}

}
