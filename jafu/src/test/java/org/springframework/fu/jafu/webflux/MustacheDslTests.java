package org.springframework.fu.jafu.webflux;

import static org.springframework.fu.jafu.Jafu.reactiveWebApplication;
import static org.springframework.fu.jafu.webflux.WebFluxServerDsl.webFlux;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;

public class MustacheDslTests {

	@Test
	void createAndRequestAMustacheView() {
		var app = reactiveWebApplication(a -> a.enable(webFlux(s -> s.port(0).mustache().router(r -> r.GET("/view", request -> ok().render("template", Collections.singletonMap("name", "world")))))));

		var context = app.run();
		var port = context.getEnvironment().getProperty("local.server.port");
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/view").exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(String.class)
				.isEqualTo("Hello world!");
		context.close();
	}

}
