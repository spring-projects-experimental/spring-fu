package org.springframework.fu.jafu.r2dbc;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.data.r2dbc.H2DatabaseClientInitializer;
import org.springframework.boot.autoconfigure.data.r2dbc.H2R2dbcProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for R2DBC H2 configuration.
 *
 * Enable and configure R2DBC support by registering a {@link org.springframework.data.r2dbc.function.DatabaseClient}.
 *
 * Required dependencies are {@code io.r2dbc:r2dbc-h2} and {@code org.springframework.data:spring-data-r2dbc}.
 *
 * @author Sebastien Deleuze
 */
public class H2R2dbcDsl extends AbstractDsl {

	private final Consumer<H2R2dbcDsl> dsl;

	private final H2R2dbcProperties properties = new H2R2dbcProperties();

	H2R2dbcDsl(Consumer<H2R2dbcDsl> dsl) {
		this.dsl = dsl;
	}

	/**
	 * Configure R2DBC H2 support with default properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.data.r2dbc.function.DatabaseClient
	 */
	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcH2() {
		return new H2R2dbcDsl(mongoDsl -> {});
	}

	/**
	 * Configure R2DBC H2 support with customized properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.data.r2dbc.function.DatabaseClient
	 */
	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcH2(Consumer<H2R2dbcDsl> dsl) {
		return new H2R2dbcDsl(dsl);
	}

	/**
	 * Configure the database url, {@code mem:test;DB_CLOSE_DELAY=-1} by default.
	 * Includes everything after the {@code jdbc:h2:} prefix.
	 * For in-memory and file-based databases, must include the proper prefix (e.g. {@code file:} or {@code mem:}).
	 *
	 * See <a href="http://www.h2database.com/html/features.html#database_url">http://www.h2database.com/html/features.html#database_url</a> for more details.
	 */
	public H2R2dbcDsl url(String url) {
		properties.setUrl(url);
		return this;
	}

	/**
	 * Configure the username.
	 */
	public H2R2dbcDsl username(String username) {
		properties.setUsername(username);
		return this;
	}

	/**
	 * Configure the password.
	 */
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
