package org.springframework.fu.jafu.webmvc;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.fu.jafu.Jafu.webApplication;
import static org.springframework.fu.jafu.webmvc.WebMvcServerDsl.webMvc;
import static org.springframework.web.servlet.function.ServerResponse.noContent;

public class WebServerDslTests {

	@Test
	public void emptyServer() {
		var app = webApplication(a -> a.enable(webMvc(s -> s.port(0))));
		var context = app.run();
		context.close();
	}

	@Test
	void createAndRequestAnEndpoint() {
		var app = webApplication(a -> a.enable(
				webMvc(s -> s.port(0).router(r -> r.GET("/foo", request -> noContent().build())))));

		var context = app.run();
		var port = context.getEnvironment().getProperty("local.server.port");
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/foo"). accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful();
		context.close();
	}

	@Test
	void requestStaticFile() {
		var app = webApplication(a -> a.enable(
				webMvc(s -> s.port(0))));

		var context = app.run();
		var port = context.getEnvironment().getProperty("local.server.port");
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/test.txt").exchange().expectBody(String.class).isEqualTo("Test");
		context.close();
	}
}
