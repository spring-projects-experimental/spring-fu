package org.springframework.fu.jafu.templating;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.mustache.MustacheInitializer;
import org.springframework.boot.autoconfigure.mustache.MustacheProperties;
import org.springframework.boot.autoconfigure.mustache.MustacheReactiveWebInitializer;
import org.springframework.boot.autoconfigure.mustache.MustacheServletWebInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for Mustache template engine.
 *
 * Configure a <a href="https://github.com/samskivert/jmustache">Mustache</a> view resolver.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-mustache}.
 *
 * @author Sebastien Deleuze
 */
public class MustacheDsl extends AbstractDsl {

	private final Consumer<MustacheDsl> dsl;

	private final MustacheProperties properties = new MustacheProperties();

	public MustacheDsl(Consumer<MustacheDsl> dsl) {
		this.dsl = dsl;
	}

	/**
	 * Prefix to apply to template names.
	 */
	public MustacheDsl prefix(String prefix) {
		this.properties.setPrefix(prefix);
		return this;
	}

	/**
	 * Suffix to apply to template names.
	 */
	public MustacheDsl suffix(String suffix) {
		this.properties.setSuffix(suffix);
		return this;
	}

	public void initializeServlet(GenericApplicationContext context) {
		this.initialize(context);
		new MustacheServletWebInitializer(properties).initialize(context);
	}

	public void initializeReactive(GenericApplicationContext context) {
		this.initialize(context);
		new MustacheReactiveWebInitializer(properties).initialize(context);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		new MustacheInitializer(properties).initialize(context);
	}
}
