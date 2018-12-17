package org.springframework.fu.jafu.r2dbc;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.data.r2dbc.H2DatabaseClientInitializer;
import org.springframework.boot.autoconfigure.data.r2dbc.H2R2dbcProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for R2DBC configuration.
 * @author Sebastien Deleuze
 */
public class H2R2dbcDsl extends AbstractDsl {

	private final Consumer<H2R2dbcDsl> dsl;

	private final H2R2dbcProperties properties = new H2R2dbcProperties();

	H2R2dbcDsl(Consumer<H2R2dbcDsl> dsl) {
		this.dsl = dsl;
	}

	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcH2() {
		return new H2R2dbcDsl(mongoDsl -> {});
	}

	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcH2(Consumer<H2R2dbcDsl> dsl) {
		return new H2R2dbcDsl(dsl);
	}

	public H2R2dbcDsl url(String url) {
		properties.setUrl(url);
		return this;
	}

	public H2R2dbcDsl username(String username) {
		properties.setUsername(username);
		return this;
	}

	public H2R2dbcDsl password(String password) {
		properties.setPassword(password);
		return this;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		if (properties.getUrl() == null) {
			properties.setUrl("mem:test;DB_CLOSE_DELAY=-1");
		}
		new H2DatabaseClientInitializer(properties).initialize(context);
	}
}
