package org.springframework.fu.jafu.web;

import java.util.function.Consumer;

import org.springframework.fu.jafu.AbstractDsl;

abstract class WebFluxCodecDsl extends AbstractDsl {

	/**
	 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder}
	 */
	abstract public WebFluxCodecDsl string();

	/**
	 * Enable {@link org.springframework.http.codec.ResourceHttpMessageWriter} and {@link org.springframework.core.codec.ResourceDecoder}
	 */
	abstract public WebFluxCodecDsl resource();

	/**
	 * Enable {@link org.springframework.http.codec.protobuf.ProtobufEncoder} and {@link org.springframework.http.codec.protobuf.ProtobufDecoder}
	 *
	 * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
	 * supports `application/x-protobuf` and `application/octet-stream`.
	 */
	abstract public WebFluxCodecDsl protobuf();

	/**
	 * Enable {@link org.springframework.http.codec.FormHttpMessageWriter} and {@link org.springframework.http.codec.FormHttpMessageReader}
	 */
	abstract WebFluxCodecDsl form();

	/**
	 * Enable {@link org.springframework.http.codec.multipart.MultipartHttpMessageWriter} and
	 * {@link org.springframework.http.codec.multipart.MultipartHttpMessageReader}
	 *
	 * This codec requires Synchronoss NIO Multipart library via  the {@code org.synchronoss.cloud:nio-multipart-parser} dependency.
	 */
	abstract WebFluxCodecDsl multipart();

	/**
	 * @see #jackson(Consumer)
	 */
	abstract WebFluxCodecDsl jackson();

	/**
	 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
	 * JSON codec on WebFlux client via a [dedicated DSL][JacksonDsl].
	 *
	 * Require `org.springframework.boot:spring-boot-starter-json` dependency
	 * (included by default in `spring-boot-starter-webflux`).
	 */
	abstract WebFluxCodecDsl jackson(Consumer<JacksonDsl> dsl);

}