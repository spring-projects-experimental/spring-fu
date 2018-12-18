package org.springframework.fu.jafu.web;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.web.reactive.FormCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.MultipartCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ProtobufCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientBuilderInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for WebFlux client configuration.
 *
 * @author Sebastien Deleuze
 */
public class WebFluxClientDsl extends AbstractDsl {

	private final Consumer<WebFluxClientDsl> dsl;

	private boolean codecsConfigured = false;

	private String baseUrl = null;

	private WebFluxClientDsl(Consumer<WebFluxClientDsl> dsl) {
		this.dsl = dsl;
	}

	public static ApplicationContextInitializer<GenericApplicationContext> client() {
		return new WebFluxClientDsl(webFluxClientDsl -> {});
	}

	public static ApplicationContextInitializer<GenericApplicationContext> client(Consumer<WebFluxClientDsl> dsl) {
		return new WebFluxClientDsl(dsl);
	}

	/**
	 * Configure a base URL for requests performed through the client.
	 */
	public WebFluxClientDsl baseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	/**
	 * Configure codecs via a [dedicated DSL][WebFluxServerCodecDsl].
	 * @see WebFluxCodecDsl#resource
	 * @see WebFluxCodecDsl#string
	 * @see WebFluxCodecDsl#protobuf
	 * @see WebFluxCodecDsl#form
	 * @see WebFluxCodecDsl#multipart
	 * @see WebFluxServerDsl.WebFluxServerCodecDsl#jackson
	 */
	public WebFluxClientDsl codecs(Consumer<WebFluxClientCodecDsl> init) {
		new WebFluxClientCodecDsl(init).initialize(context);
		this.codecsConfigured = true;
		return this;
	}

	@Override
	public WebFluxClientDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
		return (WebFluxClientDsl) super.enable(dsl);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		if (!this.codecsConfigured) {
			new StringCodecInitializer(true).initialize(context);
			new ResourceCodecInitializer(true).initialize(context);
		}
		new ReactiveWebClientBuilderInitializer(baseUrl).initialize(context);
	}

	static public class WebFluxClientCodecDsl extends AbstractDsl implements WebFluxCodecDsl {

		private final Consumer<WebFluxClientCodecDsl> dsl;

		public WebFluxClientCodecDsl(Consumer<WebFluxClientCodecDsl> dsl) {
			this.dsl = dsl;
		}

		@Override
		public WebFluxClientCodecDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
			return (WebFluxClientCodecDsl) super.enable(dsl);
		}

		@Override
		public void initialize(GenericApplicationContext context) {
			super.initialize(context);
			this.dsl.accept(this);
		}

		@Override
		public WebFluxCodecDsl string() {
			new StringCodecInitializer(true).initialize(context);
			return this;
		}

		@Override
		public WebFluxCodecDsl resource() {
			new ResourceCodecInitializer(true).initialize(context);
			return this;
		}

		@Override
		public WebFluxCodecDsl protobuf() {
			new ProtobufCodecInitializer(true).initialize(context);
			return this;
		}

		@Override
		public WebFluxCodecDsl form() {
			new FormCodecInitializer(true).initialize(context);
			return this;
		}

		@Override
		public WebFluxCodecDsl multipart() {
			new MultipartCodecInitializer(true).initialize(context);
			return this;
		}

		@Override
		public WebFluxCodecDsl jackson() {
			return jackson(dsl -> {});
		}

		@Override
		public WebFluxCodecDsl jackson(Consumer<JacksonDsl> dsl) {
			new JacksonDsl(true, dsl).initialize(context);
			return this;
		}
	}

}
