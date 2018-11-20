/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.jafu.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.fu.jafu.ApplicationDsl.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.fu.jafu.ApplicationDsl;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.test.StepVerifier;

/**
 * @author Sebastien Deleuze
 * @author Alexey Nesterov
 */
abstract class AbstractWebServerDslTests {

	protected int port = 8080;

	abstract ConfigurableReactiveWebServerFactory getServerFactory();

	@Test
	void createAnApplicationWithAnEmptyServer() {
		ApplicationDsl app = application(a -> a.server(s -> s.engine(getServerFactory())));
		ConfigurableApplicationContext context = app.run();
		context.close();
	}

	@Test
	void createAndRequestAnEndpoint() {
		RouterFunction<ServerResponse> router = route().GET("/foo", request -> noContent().build()).build();
		ApplicationDsl app = application(a -> a.server(s -> s.engine(getServerFactory()).importRouter(router)));

		ConfigurableApplicationContext context = app.run();
		WebTestClient client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/foo"). accept(MediaType.TEXT_PLAIN).exchange().expectStatus().is2xxSuccessful();
		context.close();
	}

	@Test
	void createAWebClientAndRequestAnEndpoint() {
		RouterFunction<ServerResponse> router = route().GET("/", request -> noContent().build()).build();
		ApplicationDsl app = application(a -> a.server(s -> s.engine(getServerFactory()).importRouter(router)).client(c -> c.baseUrl("http://127.0.0.1:" + port)));

		ConfigurableApplicationContext context = app.run();
		WebClient client = context.getBean(WebClient.Builder.class).build();
		StepVerifier.create(client.get().uri("/").exchange())
					.consumeNextWith(consumer -> assertEquals(NO_CONTENT, consumer.statusCode()))
					.verifyComplete();
		context.close();
	}

	@Test
	void declare2RouterBlocks() {
		RouterFunction<ServerResponse> router1 = route().GET("/foo", request -> noContent().build()).build();
		RouterFunction<ServerResponse> router2 = route().GET("/bar", request -> ok().build()).build();

		ApplicationDsl app = application(a -> a.server(s -> s.engine(getServerFactory())
				.importRouter(router1)
				.importRouter(router2)));
		ConfigurableApplicationContext context = app.run();
		WebTestClient client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/foo").exchange().expectStatus().isNoContent();
		client.get().uri("/bar").exchange().expectStatus().isOk();
		context.close();
	}

	@Test
	void declare2ServerBlocks() {
		ApplicationDsl app = application(a -> a
				.server(s -> s.engine(getServerFactory()))
				.server(s -> s.engine(getServerFactory()).port(8181)));
		Assertions.assertThrows(IllegalStateException.class, app::run);
	}

	@Test
	void checkThatConcurrentModificationExceptionIsNotThrown() {
		RouterFunction<ServerResponse> router = route().GET("/", request -> noContent().build()).build();

		ApplicationDsl app = application(a -> a.server(s -> s.engine(getServerFactory())
				.codecs(c -> c.string().jackson())
				.importRouter(router))
			.logging(l -> l.level(LogLevel.DEBUG))
			.mongodb(m -> m.embedded()));
		ConfigurableApplicationContext context = app.run();
		WebTestClient client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
		client.get().uri("/").exchange().expectStatus().is2xxSuccessful();
		context.close();
	}

}