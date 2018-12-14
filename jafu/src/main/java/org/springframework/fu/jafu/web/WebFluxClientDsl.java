package org.springframework.fu.jafu.web;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.web.reactive.FormCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.MultipartCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ProtobufCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientBuilderInitializer;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.Dsl;

public class WebFluxClientDsl extends AbstractDsl {

	private final Consumer<WebFluxClientDsl> dsl;

	private boolean codecsConfigured = false;

	private String baseUrl = null;

	private WebFluxClientDsl(Consumer<WebFluxClientDsl> dsl) {
		this.dsl = dsl;
	}

	public static Dsl client() {
		return new WebFluxClientDsl(webFluxClientDsl -> {});
	}

	public static Dsl client(Consumer<WebFluxClientDsl> dsl) {
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
	public void register() {
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
		public void register() {
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
		WebFluxCodecDsl form() {
			new FormCodecInitializer(true).initialize(context);
			return this;
		}

		@Override
		WebFluxCodecDsl multipart() {
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
