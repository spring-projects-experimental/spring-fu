package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.context.properties.FunctionalConfigurationPropertiesBinder;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.web.ServerDsl;

/**
 * Jafu DSL for modular configuration that can be imported in the application.
 *
 * @see ApplicationDsl#importConfiguration(Consumer)
 * @author Sebastien Deleuze
 */
public class ConfigurationDsl extends AbstractDsl {

	private final Consumer<ConfigurationDsl> dsl;

	public ConfigurationDsl(Consumer<ConfigurationDsl> dsl) {
		super();
		this.dsl = dsl;
	}

	public void server(Consumer<ServerDsl> dsl) {
		ServerProperties serverProperties = new ServerProperties();
		ResourceProperties resourceProperties = new ResourceProperties();
		WebFluxProperties webFluxProperties = new WebFluxProperties();
		ConfigurableReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory();
		this.initializers.add(new StringCodecInitializer(false));
		this.initializers.add(new ResourceCodecInitializer(false));
		this.initializers.add(new ReactiveWebServerInitializer(serverProperties, resourceProperties, webFluxProperties, serverFactory));
		this.initializers.add(new ServerDsl(dsl));
	}

	public void logging(Consumer<LoggingDsl> dsl) {
		new LoggingDsl(dsl);
	}

	public <T> void properties(Class<T> clazz) {
		properties(clazz, "");
	}

	public <T> void properties(Class<T> clazz, String prefix) {
		context.registerBean(clazz.getSimpleName() + "ConfigurationProperties", clazz, () -> new FunctionalConfigurationPropertiesBinder(context).bind(prefix, Bindable.of(clazz)).get());
	}

	public void beans(Consumer<BeanDsl> dsl) {
		this.initializers.add(new BeanDsl(dsl));
	}

	@Override
	public void register(GenericApplicationContext context) {
		this.dsl.accept(this);
	}

}
