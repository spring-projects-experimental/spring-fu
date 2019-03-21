/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.jafu.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.fu.jafu.Jafu.webApplication;
import static org.springframework.fu.jafu.mongo.MongoDsl.mongo;
import static org.springframework.fu.jafu.web.WebFluxClientDsl.client;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

/**
 * @author Sebastien Deleuze
 * @author Alexey Nesterov
 */
class WebServerDslTests {

	private int port = 8080;

	@Test
	void createAnApplicationWithAnEmptyServer() {
		var app = webApplication(a -> a.enable(server()));
		var context = app.run();
		context.close();
	}

	@Test
	void createAnApplicationWithAServerAndAFilter() {
		var app = webApplication(a -> a.enable(server(s -> s.filter(MyFilter.class))));
		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/foo"). accept(MediaType.TEXT_PLAIN).exchange().expectStatus().isEqualTo(UNAUTHORIZED);
		context.close();
	}

	@Test
	void createAndRequestAnEndpoint() {
		var app = webApplication(a -> a.enable(server(s -> s.router(r -> r.GET("/foo", request -> noContent().build())))));

		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/foo"). accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful();
		context.close();
	}

	@Test
	void createAndRequestAnEndpointWithACustomizedEngine() {
		var app = webApplication(a -> a.enable(server(s -> s.engine(new TomcatReactiveWebServerFactory()).router(r -> r.GET("/foo", request -> noContent().build())))));

		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/foo"). accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful();
		context.close();
	}

	@Test
	void createAWebClientAndRequestAnEndpoint() {
		var app = webApplication(a ->
				a.enable(server(s -> s.router(r -> r.GET("/", request -> noContent().build()))))
				.enable(client(c -> c.baseUrl("http://127.0.0.1:" + port))));

		var context = app.run();
		var client = context.getBean(WebClient.Builder.class).build();
		StepVerifier.create(client.get().uri("/").exchange())
					.consumeNextWith(consumer -> assertEquals(NO_CONTENT, consumer.statusCode()))
					.verifyComplete();
		context.close();
	}

	@Test
	void declare2RouterBlocks() {
		var app = webApplication(a -> a.enable(server(s -> s
				.router(r -> r.GET("/foo", request -> noContent().build()))
				.router(r -> r.GET("/bar", request -> ok().build())))));
		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/foo").exchange().expectStatus().isNoContent();
		client.get().uri("/bar").exchange().expectStatus().isOk();
		context.close();
	}

	@Test
	void declare2ServerBlocks() {
		var app = webApplication(a -> a
				.enable(server())
				.enable(server(s -> s.port(8181))));
		Assertions.assertThrows(IllegalStateException.class, app::run);
	}

	@Test
	void checkThatConcurrentModificationExceptionIsNotThrown() {
		var app = webApplication(a ->
				a.enable(server(s -> s
				.codecs(c -> c.string().jackson())
				.router(r -> r.GET("/", request -> noContent().build()))))
			.logging(l -> l.level(LogLevel.DEBUG))
			.enable(mongo()));
		var context = app.run();
		var client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/").exchange().expectStatus().is2xxSuccessful();
		context.close();
	}

	@Test
	void runAnApplication2Times() {
		var app = webApplication(a -> a.enable(server()));
		var context = app.run();
		context.close();
		context = app.run();
		context.close();
	}

	static class MyFilter implements WebFilter {

		@Override
		public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
			exchange.getResponse().setStatusCode(UNAUTHORIZED);
			return Mono.empty();
		}
	}

}