package com.sample;

import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

public class UserHandler {

	private final UserRepository repository;

	public UserHandler(UserRepository repository) {
		this.repository = repository;
	}

	public Mono<ServerResponse> listApi(ServerRequest request) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(repository.findAll(), User.class);
	}

}
