package org.springframework.fu.jafu.web;

import static org.springframework.fu.jafu.Jafu.webApplication;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;

public class MustacheDslTests {

	@Test
	void createAndRequestAMustacheView() {
		var router = RouterFunctions
				.route()
				.GET("/view", request -> ok().render("template", Collections.singletonMap("name", "world")))
				.build();
		var app = webApplication(a -> a.enable(server(s -> s.mustache().include(router))));

		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://0.0.0.0:8080").build();
		client.get().uri("/view").exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(String.class)
				.isEqualTo("Hello world!");
		context.close();
	}

}
