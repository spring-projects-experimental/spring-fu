package org.springframework.fu.jafu.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.fu.jafu.Jafu.webApplication;
import static org.springframework.fu.jafu.web.WebFluxClientDsl.client;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

public class JacksonDslTests {

	@Test
	void enableJacksonModuleOnServerCreateAndRequestAJSONEndpoint() {
		var router = route()
				.GET("/user", request -> ok().header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE).syncBody(new User("Brian")))
				.build();

		var app = webApplication(a -> a.enable(server(s -> s.codecs(c -> c.jackson()).include(router))));

		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:8080").build();
		client.get().uri("/user").exchange()
				.expectStatus().is2xxSuccessful()
				.expectHeader().contentType(APPLICATION_JSON_UTF8_VALUE)
				.expectBody(User.class)
				.isEqualTo(new User("Brian"));
		context.close();
	}

	@Test
	void enableJacksonModuleOnClientAndServerCreateAndRequestAJSONEndpoint() {
		var router = route()
				.GET("/user", request -> ok().header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE).syncBody(new User("Brian")))
				.build();

		var app = webApplication(a ->
				a.enable(server(s -> s.codecs(c -> c.jackson()).include(router)))
				.enable(client(c -> c.codecs(codecs -> codecs.jackson()))));
		var context = app.run();
		var client = context.getBean(WebClient.Builder.class).build();
		var response = client.get().uri("http://127.0.0.1:8080/user").exchange();

		StepVerifier.create(response)
					.consumeNextWith(it -> {
						assertEquals(HttpStatus.OK, it.statusCode());
						assertEquals(APPLICATION_JSON_UTF8, it.headers().contentType().get());
					})
					.verifyComplete();
		var mappers = context.getBeanProvider(ObjectMapper.class).stream().collect(Collectors.toList());
		assertEquals(1, mappers.size());
		context.close();
	}

	@Test
	void noJacksonCodecOnServerWhenNotDeclared() {
		var router = route()
				.GET("/user", request -> ok().header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE).syncBody(new User("Brian")))
				.build();


		var app = webApplication(a -> a.enable(server(s -> s.include(router))));
		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:8080").build();
		client.get().uri("/user").exchange().expectStatus().is5xxServerError();
		context.close();
	}

	private static class User {

		private String name;

		public User() {
		}

		public User(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			var user = (User) o;
			return Objects.equals(name, user.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}

}
