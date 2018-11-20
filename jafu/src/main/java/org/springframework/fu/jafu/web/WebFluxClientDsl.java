package org.springframework.fu.jafu.web;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.web.reactive.FormCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.MultipartCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ProtobufCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientBuilderInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

public class WebFluxClientDsl extends AbstractDsl {

	private final Consumer<WebFluxClientDsl> dsl;

	private boolean codecsConfigured = false;

	private String baseUrl = null;

	public WebFluxClientDsl(Consumer<WebFluxClientDsl> dsl) {
		super();
		this.dsl = dsl;
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
		addInitializer(new WebFluxClientCodecDsl(init));
		this.codecsConfigured = true;
		return this;
	}

	@Override
	public void register(GenericApplicationContext context) {
		this.dsl.accept(this);
		if (!this.codecsConfigured) {
			new StringCodecInitializer(true).initialize(context);
			new ResourceCodecInitializer(true).initialize(context);
		}
		new ReactiveWebClientBuilderInitializer(baseUrl).initialize(context);
	}

	static public class WebFluxClientCodecDsl extends WebFluxCodecDsl {

		private final Consumer<WebFluxClientCodecDsl> dsl;

		public WebFluxClientCodecDsl(Consumer<WebFluxClientCodecDsl> dsl) {
			this.dsl = dsl;
		}

		@Override
		public void register(GenericApplicationContext context) {
			this.dsl.accept(this);
		}

		@Override
		public WebFluxCodecDsl string() {
			addInitializer(new StringCodecInitializer(true));
			return this;
		}

		@Override
		public WebFluxCodecDsl resource() {
			addInitializer(new ResourceCodecInitializer(true));
			return this;
		}

		@Override
		public WebFluxCodecDsl protobuf() {
			addInitializer(new ProtobufCodecInitializer(true));
			return this;
		}

		@Override
		WebFluxCodecDsl form() {
			addInitializer(new FormCodecInitializer(true));
			return this;
		}

		@Override
		WebFluxCodecDsl multipart() {
			addInitializer(new MultipartCodecInitializer(true));
			return this;
		}

		@Override
		public WebFluxCodecDsl jackson() {
			return jackson(dsl -> {});
		}

		@Override
		public WebFluxCodecDsl jackson(Consumer<JacksonDsl> dsl) {
			addInitializer(new JacksonDsl(true, dsl));
			return this;
		}
	}

}
