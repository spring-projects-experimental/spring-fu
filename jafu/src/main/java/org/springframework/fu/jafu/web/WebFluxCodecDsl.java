package org.springframework.fu.jafu.web;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

abstract class WebFluxCodecDsl extends AbstractDsl {

	@Override
	public void register(GenericApplicationContext context) {
	}

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

}