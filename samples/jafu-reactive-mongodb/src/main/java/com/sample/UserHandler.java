package com.sample;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.HashMap;
import java.util.Map;

public class UserHandler {

    private final UserRepository repository;
    private final SampleProperties properties;

    public UserHandler(UserRepository repository, SampleProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public Mono<ServerResponse> listApi(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repository.findAll(), User.class);
    }

    public Mono<ServerResponse> listView(ServerRequest request) {
        Map<String, Flux<User>> userMap = new HashMap<>();
        userMap.put("users", repository.findAll());
        return ServerResponse.ok()
                .render("users", userMap);
    }

    public Mono<ServerResponse> conf(ServerRequest request) {
        return ServerResponse
                .ok()
                .bodyValue(properties.getMessage());
    }
}
