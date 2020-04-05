package com.sample;

import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.ServerResponse.ok;

public class SampleHandler {

	private SampleService sampleService;

	public SampleHandler(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public ServerResponse hello(ServerRequest request) {
		return ok().body(sampleService.generateMessage());
	}

	public ServerResponse json(ServerRequest request) {
		return ok().body(new Sample(sampleService.generateMessage()));
	}
}
