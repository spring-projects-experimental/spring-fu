package org.springframework.fu.jafu.web;

import java.util.function.Consumer;

interface WebFluxCodecDsl {

	/**
	 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder}
	 */
	WebFluxCodecDsl string();

	/**
	 * Enable {@link org.springframework.http.codec.ResourceHttpMessageWriter} and {@link org.springframework.core.codec.ResourceDecoder}
	 */
	WebFluxCodecDsl resource();

	/**
	 * Enable {@link org.springframework.http.codec.protobuf.ProtobufEncoder} and {@link org.springframework.http.codec.protobuf.ProtobufDecoder}
	 *
	 * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
	 * supports `application/x-protobuf` and `application/octet-stream`.
	 */
	WebFluxCodecDsl protobuf();

	/**
	 * Enable {@link org.springframework.http.codec.FormHttpMessageWriter} and {@link org.springframework.http.codec.FormHttpMessageReader}
	 */
	WebFluxCodecDsl form();

	/**
	 * Enable {@link org.springframework.http.codec.multipart.MultipartHttpMessageWriter} and
	 * {@link org.springframework.http.codec.multipart.MultipartHttpMessageReader}
	 *
	 * This codec requires Synchronoss NIO Multipart library via  the {@code org.synchronoss.cloud:nio-multipart-parser} dependency.
	 */
	WebFluxCodecDsl multipart();

	/**
	 * @see #jackson(Consumer)
	 */
	WebFluxCodecDsl jackson();

	/**
	 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
	 * JSON codec on WebFlux client via a [dedicated DSL][JacksonDsl].
	 *
	 * Require `org.springframework.boot:spring-boot-starter-json` dependency
	 * (included by default in `spring-boot-starter-webflux`).
	 */
	WebFluxCodecDsl jackson(Consumer<JacksonDsl> dsl);

}