package org.springframework.fu.jafu.webflux;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.uniqueBeanName;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.FormCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.JacksonJsonCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.MultipartCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ProtobufCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.web.JacksonDsl;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.server.WebFilter;

/**
 * Jafu DSL for WebFlux server.
 *
 * This DSL to be used with {@link org.springframework.fu.jafu.Jafu#application(WebApplicationType, java.util.function.Consumer)}
 * using a {@link WebApplicationType#REACTIVE} parameter configures a
 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux"></a>WebFlux server</a>.
 *
 * When no codec is configured, {@code String} and {@code Resource} ones are configured by default.
 * When a {@code codecs} block is declared, the one specified are configured by default.
 *
 * You can chose the underlying engine via the {@link WebFluxServerDsl#engine(ConfigurableReactiveWebServerFactory)} parameter.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-webflux}.
 *
 * @see org.springframework.fu.jafu.Jafu#application(WebApplicationType, java.util.function.Consumer)
 * @see WebFluxServerDsl#codecs(Consumer)
 * @see WebFluxServerDsl#mustache()
 * @author Sebastien Deleuze
 */
public class WebFluxServerDsl extends AbstractDsl {

	private final Consumer<WebFluxServerDsl> dsl;

	private ServerProperties serverProperties = new ServerProperties();

	private ResourceProperties resourceProperties = new ResourceProperties();

	private WebFluxProperties webFluxProperties = new WebFluxProperties();

	private boolean codecsConfigured = false;

	private int port = 8080;

	private ConfigurableReactiveWebServerFactory engine = null;

	WebFluxServerDsl(Consumer<WebFluxServerDsl> dsl) {
		super();
		this.dsl = dsl;
	}

	public static ApplicationContextInitializer<GenericApplicationContext> webFlux() {
		return new WebFluxServerDsl(dsl -> {});
	}

	public static ApplicationContextInitializer<GenericApplicationContext> webFlux(Consumer<WebFluxServerDsl> dsl) {
		return new WebFluxServerDsl(dsl);
	}

	/**
	 * Define the listening port of the webFlux.
	 */
	public WebFluxServerDsl port(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Define the underlying engine used.
	 */
	public WebFluxServerDsl engine(ConfigurableReactiveWebServerFactory engine) {
		this.engine = engine;
		return this;
	}

	/**
	 * Configure routes via {@link RouterFunctions.Builder}.
	 * @see org.springframework.fu.jafu.BeanDefinitionDsl#bean(Class, BeanDefinitionCustomizer...)
	 */
	public WebFluxServerDsl router(Consumer<RouterFunctions.Builder> routerDsl) {
		RouterFunctions.Builder builder = RouterFunctions.route();
		context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunction.class.getName(), context), RouterFunction.class, () -> {
			routerDsl.accept(builder);
			return builder.build();
		});
		return this;
	}

	/**
	 * Configure codecs via a [dedicated DSL][WebFluxServerCodecDsl].
	 * @see WebFluxServerCodecDsl#jackson
	 */
	public WebFluxServerDsl codecs(Consumer<WebFluxServerCodecDsl> init) {
		new WebFluxServerCodecDsl(init).initialize(context);
		this.codecsConfigured = true;
		return this;
	}

	/**
	 * Define a request filter for this webFlux
	 */
	public WebFluxServerDsl filter(Class<? extends WebFilter> clazz) {
		context.registerBean(uniqueBeanName(clazz.getName(), context), clazz);
		return this;
	}

	/**
	 * @see #mustache(Consumer)
	 */
	public WebFluxServerDsl mustache() {
		return mustache(dsl -> {});
	}

	/**
	 * Configure Mustache view resolver.
	 *
	 * Require {@code org.springframework.boot:spring-boot-starter-mustache} dependency.
	 */
	public WebFluxServerDsl mustache(Consumer<MustacheDsl> dsl) {
		new MustacheDsl(dsl).initialize(context);
		return this;
	}

	/**
	 * Enable an external codec.
	 */
	@Override
	public WebFluxServerDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
		return (WebFluxServerDsl) super.enable(dsl);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		if (engine == null) {
			engine = new NettyDelegate().get();
		}
		engine.setPort(port);

		if (!codecsConfigured) {
			new StringCodecInitializer(false, false).initialize(context);
			new ResourceCodecInitializer(false).initialize(context);
		}
		if (context.containsBeanDefinition("webHandler")) {
			throw new IllegalStateException("Only one webFlux per application is supported");
		}
		new ReactiveWebServerInitializer(serverProperties, resourceProperties, webFluxProperties, engine).initialize(context);

	}

	/**
	 * Jafu DSL for WebFlux server codecs.
	 */
	static public class WebFluxServerCodecDsl extends AbstractDsl {

		private final Consumer<WebFluxServerCodecDsl> dsl;

		WebFluxServerCodecDsl(Consumer<WebFluxServerCodecDsl> dsl) {
			this.dsl = dsl;
		}

		@Override
		public WebFluxServerCodecDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
			return (WebFluxServerCodecDsl) super.enable(dsl);
		}

		@Override
		public void initialize(GenericApplicationContext context) {
			super.initialize(context);
			this.dsl.accept(this);
		}

		/**
		 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder} for all media types
		 */
		public WebFluxServerCodecDsl string() {
			new StringCodecInitializer(false, false).initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder}
		 */
		public WebFluxServerCodecDsl string(boolean textPlainOnly) {
			new StringCodecInitializer(false, textPlainOnly).initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.ResourceHttpMessageWriter} and {@link org.springframework.core.codec.ResourceDecoder}
		 */
		public WebFluxServerCodecDsl resource() {
			new ResourceCodecInitializer(false).initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.protobuf.ProtobufEncoder} and {@link org.springframework.http.codec.protobuf.ProtobufDecoder}
		 *
		 * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
		 * supports `application/x-protobuf` and `application/octet-stream`.
		 */
		public WebFluxServerCodecDsl protobuf() {
			new ProtobufCodecInitializer(false).initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.FormHttpMessageWriter} and {@link org.springframework.http.codec.FormHttpMessageReader}
		 */
		public WebFluxServerCodecDsl form() {
			new FormCodecInitializer(false).initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.multipart.MultipartHttpMessageWriter} and
		 * {@link org.springframework.http.codec.multipart.MultipartHttpMessageReader}
		 *
		 * This codec requires Synchronoss NIO Multipart library via  the {@code org.synchronoss.cloud:nio-multipart-parser} dependency.
		 */
		public WebFluxServerCodecDsl multipart() {
			new MultipartCodecInitializer(false).initialize(context);
			return this;
		}

		/**
		 * @see #jackson(Consumer)
		 */
		public WebFluxServerCodecDsl jackson() {
			return jackson(dsl -> {});
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON codec on WebFlux server via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-webflux`).
		 */
		public WebFluxServerCodecDsl jackson(Consumer<JacksonDsl> dsl) {
			new JacksonDsl(false, dsl).initialize(context);
			new JacksonJsonCodecInitializer(false).initialize(context);
			return this;
		}
	}

	private static class NettyDelegate implements Supplier<ConfigurableReactiveWebServerFactory> {
		@Override
		public ConfigurableReactiveWebServerFactory get() {
			return new NettyReactiveWebServerFactory();
		}
	}

}
