package com.sample;

import java.util.Collections;

import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

public class UserHandler {

	private final UserRepository repository;

	private final SampleProperties properties;

	public UserHandler(UserRepository repository, SampleProperties properties) {
		this.repository = repository;
		this.properties = properties;
	}

	public Mono<ServerResponse> listApi(ServerRequest request) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(repository.findAll(), User.class);
	}

	public Mono<ServerResponse> listView(ServerRequest request) {
		return ServerResponse.ok().render("users", Collections.singletonMap("users", repository.findAll()));
	}



	public Mono<ServerResponse> conf(ServerRequest request) {
		return ServerResponse.ok().syncBody(properties.getMessage());
	}

}
