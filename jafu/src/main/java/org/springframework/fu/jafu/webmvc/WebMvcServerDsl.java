package org.springframework.fu.jafu.webmvc;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.AtomConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.FormConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.JacksonJsonConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.ResourceConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.RssConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerInitializer;
import org.springframework.boot.autoconfigure.web.servlet.StringConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.templating.MustacheDsl;
import org.springframework.fu.jafu.templating.ThymeleafDsl;
import org.springframework.fu.jafu.web.JacksonDsl;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;

/**
 * Jafu DSL for WebMvc server.
 *
 * This DSL to be used with {@link org.springframework.fu.jafu.Jafu#webApplication(Consumer)}
 * configures a <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#webmvc-fn"></a>WebMvc server</a>.
 *
 * When no converter is configured, {@code String} and {@code Resource} ones are configured by default.
 * When a {@code converters} block is declared, the one specified are configured by default.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-web}.
 *
 * @see org.springframework.fu.jafu.Jafu#webApplication(Consumer)
 * @see WebMvcServerDsl#converters(Consumer)
 * @author Sebastien Deleuze
 */
public class WebMvcServerDsl extends AbstractDsl {

	private final Consumer<WebMvcServerDsl> dsl;

	private ServerProperties serverProperties = new ServerProperties();

	private ResourceProperties resourceProperties = new ResourceProperties();

	private WebProperties webProperties = new WebProperties();

	private WebMvcProperties webMvcProperties = new WebMvcProperties();

	private ConfigurableServletWebServerFactory engine = null;

	private boolean convertersConfigured = false;

	private int port = 8080;

	WebMvcServerDsl(Consumer<WebMvcServerDsl> dsl) {
		super();
		this.dsl = dsl;
	}

	public static ApplicationContextInitializer<GenericApplicationContext> webMvc() {
		return new WebMvcServerDsl(dsl -> {});
	}

	public static ApplicationContextInitializer<GenericApplicationContext> webMvc(Consumer<WebMvcServerDsl> dsl) {
		return new WebMvcServerDsl(dsl);
	}

	/**
	 * Define the listening port of the webFlux.
	 */
	public WebMvcServerDsl port(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Enable Tomcat engine.
	 */
	public WebMvcServerDsl tomcat() {
		this.engine = new TomcatDelegate().get();
		return this;
	}

	/**
	 * Enable Jetty engine.
	 */
	public WebMvcServerDsl jetty() {
		this.engine = new JettyDelegate().get();
		return this;
	}

	/**
	 * Enable Undertow engine.
	 */
	public WebMvcServerDsl undertow() {
		this.engine = new UndertowDelegate().get();
		return this;
	}

	/**
	 * Configure routes via {@link RouterFunctions.Builder}.
	 * @see org.springframework.fu.jafu.BeanDefinitionDsl#bean(Class, BeanDefinitionCustomizer...)
	 */
	public WebMvcServerDsl router(Consumer<RouterFunctions.Builder> routerDsl) {
		RouterFunctions.Builder builder = RouterFunctions.route();
		context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunction.class.getName(), context), RouterFunction.class, () -> {
			routerDsl.accept(builder);
			return builder.build();
		});
		return this;
	}

	/**
	 * Configure converters via a dedicated DSL.
	 * @see WebMvcServerConverterDsl#jackson
	 */
	public WebMvcServerDsl converters(Consumer<WebMvcServerConverterDsl> init) {
		new WebMvcServerConverterDsl(init).initialize(context);
		this.convertersConfigured = true;
		return this;
	}

	/**
	 * Enable an external codec.
	 */
	@Override
	public WebMvcServerDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
		return (WebMvcServerDsl) super.enable(dsl);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunction.class.getName(), context), RouterFunction.class, () ->
			RouterFunctions.route().resources("/**", new ClassPathResource("static/")).build()
		);
		serverProperties.setPort(port);
		if (engine == null) {
			engine = new TomcatDelegate().get();
		}
		engine.setPort(port);
		serverProperties.getServlet().setRegisterDefaultServlet(false);
		if (!convertersConfigured) {
			new StringConverterInitializer().initialize(context);
			new ResourceConverterInitializer().initialize(context);
		}
		if (context.containsBeanDefinition("webHandler")) {
			throw new IllegalStateException("Only one webFlux per application is supported");
		}
		new ServletWebServerInitializer(serverProperties, webMvcProperties, resourceProperties, webProperties, engine).initialize(context);
	}

	private class TomcatDelegate implements Supplier<ConfigurableServletWebServerFactory> {
		@Override
		public ConfigurableServletWebServerFactory get() {
			return new TomcatServletWebServerFactory();
		}
	}

	private class JettyDelegate implements Supplier<ConfigurableServletWebServerFactory> {
		@Override
		public ConfigurableServletWebServerFactory get() {
			return new JettyServletWebServerFactory();
		}
	}

	private class UndertowDelegate implements Supplier<ConfigurableServletWebServerFactory> {
		@Override
		public ConfigurableServletWebServerFactory get() {
			return new UndertowServletWebServerFactory();
		}
	}

	/**
	 * @see #thymeleaf(Consumer)
	 */
	public WebMvcServerDsl thymeleaf() {
		return thymeleaf(dsl -> {});
	}

	/**
	 * Configure Thymeleaf view resolver.
	 *
	 * Require {@code org.springframework.boot:spring-boot-starter-thymeleaf} dependency.
	 */
	public WebMvcServerDsl thymeleaf(Consumer<ThymeleafDsl> dsl) {
		new ThymeleafDsl(dsl).initializeServlet(context);
		return this;
	}

	/**
	 * @see #mustache(Consumer)
	 */
	public WebMvcServerDsl mustache() {
		return mustache(dsl -> {});
	}

	/**
	 * Configure Mustache view resolver.
	 *
	 * Require {@code org.springframework.boot:spring-boot-starter-mustache} dependency.
	 */
	public WebMvcServerDsl mustache(Consumer<MustacheDsl> dsl) {
		new MustacheDsl(dsl).initializeServlet(context);
		return this;
	}


	/**
	 * Jafu DSL for WebMvc server codecs.
	 */
	static public class WebMvcServerConverterDsl extends AbstractDsl {

		private final Consumer<WebMvcServerConverterDsl> dsl;

		WebMvcServerConverterDsl(Consumer<WebMvcServerConverterDsl> dsl) {
			this.dsl = dsl;
		}

		@Override
		public WebMvcServerConverterDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
			return (WebMvcServerConverterDsl) super.enable(dsl);
		}

		@Override
		public void initialize(GenericApplicationContext context) {
			super.initialize(context);
			this.dsl.accept(this);
		}

		/**
		 * Enable {@link org.springframework.http.converter.StringHttpMessageConverter} for all media types
		 */
		public WebMvcServerConverterDsl string() {
			new StringConverterInitializer().initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.ResourceHttpMessageConverter} and {@link org.springframework.http.converter.ResourceRegionHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl resource() {
			new ResourceConverterInitializer().initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl form() {
			new FormConverterInitializer().initialize(context);
			return this;
		}

		/**
		 * @see #jackson(Consumer)
		 */
		public WebMvcServerConverterDsl jackson() {
			return jackson(dsl -> {});
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON converter on WebMvc server via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-web`).
		 */
		public WebMvcServerConverterDsl jackson(Consumer<JacksonDsl> dsl) {
			new JacksonDsl(false, dsl).initialize(context);
			new JacksonJsonConverterInitializer().initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.feed.AtomFeedHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl  atom() {
			new AtomConverterInitializer().initialize(context);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.feed.RssChannelHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl  rss() {
			new RssConverterInitializer().initialize(context);
			return this;
		}
	}

}
