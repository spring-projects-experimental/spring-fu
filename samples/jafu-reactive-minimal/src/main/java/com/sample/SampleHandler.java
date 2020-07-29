package com.sample;

import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class SampleHandler {

	private SampleService sampleService;

	public SampleHandler(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public Mono<ServerResponse> hello(ServerRequest request) {
		return ok().bodyValue(sampleService.generateMessage());
	}

	public Mono<ServerResponse> json(ServerRequest request) {
		return ok().bodyValue(new Sample(sampleService.generateMessage()));
	}
}
