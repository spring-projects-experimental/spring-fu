package org.springframework.fu.jafu.templating;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafInitializer;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafReactiveWebInitializer;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafServletWebInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

import java.util.function.Consumer;

/**
 * Jafu DSL for Thymeleaf template engine.
 *
 * Configure a <a href="https://github.com/samskivert/jmustache">Mustache</a> view resolver.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-mustache}.
 *
 * @author Sebastien Deleuze
 */
public class ThymeleafDsl extends AbstractDsl {

	private final Consumer<ThymeleafDsl> dsl;

	protected final ThymeleafProperties properties = new ThymeleafProperties();

	public ThymeleafDsl(Consumer<ThymeleafDsl> dsl) {
		this.dsl = dsl;
	}

	/**
	 * Prefix to apply to template names.
	 */
	public ThymeleafDsl prefix(String prefix) {
		this.properties.setPrefix(prefix);
		return this;
	}

	/**
	 * Suffix to apply to template names.
	 */
	public ThymeleafDsl suffix(String suffix) {
		this.properties.setSuffix(suffix);
		return this;
	}

	public void initializeServlet(GenericApplicationContext context) {
		this.initialize(context);
		new ThymeleafServletWebInitializer(properties).initialize(context);
	}

	public void initializeReactive(GenericApplicationContext context) {
		this.initialize(context);
		new ThymeleafReactiveWebInitializer(properties).initialize(context);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		new ThymeleafInitializer(properties).initialize(context);
	}

}

