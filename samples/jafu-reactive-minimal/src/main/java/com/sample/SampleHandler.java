package com.sample;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

public class SampleHandler {

	private SampleService sampleService;

	public SampleHandler(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public Mono<ServerResponse> hello(ServerRequest request) {
		return ok().syncBody(sampleService.generateMessage());
	}

	public Mono<ServerResponse> json(ServerRequest request) {
		return ok().syncBody(new Sample(sampleService.generateMessage()));
	}
}
