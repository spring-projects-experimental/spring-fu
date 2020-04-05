package org.springframework.fu.jafu.r2dbc;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlDatabaseClientInitializer;
import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlR2dbcProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for R2DBC Postgresql configuration.
 *
 * Enable and configure R2DBC support by registering a {@link org.springframework.data.r2dbc.function.DatabaseClient}.
 *
 * Required dependencies are {@code io.r2dbc:r2dbc-postgresql} and {@code org.springframework.data:spring-data-r2dbc}.
 *
 * @author Sebastien Deleuze
 */
public class PostgresqlR2dbcDsl extends AbstractDsl {

	private final Consumer<PostgresqlR2dbcDsl> dsl;

	private final PostgresqlR2dbcProperties properties = new PostgresqlR2dbcProperties();


	PostgresqlR2dbcDsl(Consumer<PostgresqlR2dbcDsl> dsl) {
		this.dsl = dsl;
	}

	/**
	 * Configure R2DBC Postgresql support with default properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.data.r2dbc.function.DatabaseClient
	 */
	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcPostgresql() {
		return new PostgresqlR2dbcDsl(mongoDsl -> {});
	}

	/**
	 * Configure R2DBC Postgresql support with customized properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.data.r2dbc.function.DatabaseClient
	 */
	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcPostgresql(Consumer<PostgresqlR2dbcDsl> dsl) {
		return new PostgresqlR2dbcDsl(dsl);
	}

	/**
	 * Configure the host, by default set to {@code localhost}.
	 */
	public PostgresqlR2dbcDsl host(String host) {
		properties.setHost(host);
		return this;
	}

	/**
	 * Configure the port, by default set to {@code 5432}.
	 */
	public PostgresqlR2dbcDsl port(Integer port) {
		properties.setPort(port);
		return this;
	}

	/**
	 * Configure the database, by default set to {@code postgres}.
	 */
	public PostgresqlR2dbcDsl database(String database) {
		properties.setDatabase(database);
		return this;
	}

	/**
	 * Configure the username, by default set to {@code postgres}.
	 */
	public PostgresqlR2dbcDsl username(String username) {
		properties.setUsername(username);
		return this;
	}

	/**
	 * Configure the password, empty by default.
	 */
	public PostgresqlR2dbcDsl password(String password) {
		properties.setPassword(password);
		return this;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		if (properties.getHost() == null) {
			properties.setHost("localhost");
		}
		if (properties.getPort() == null) {
			properties.setPort(5432);
		}
		if (properties.getDatabase() == null) {
			properties.setDatabase("postgres");
		}
		if (properties.getUsername() == null) {
			properties.setUsername("postgres");
		}
		if (properties.getPassword() == null) {
			properties.setPassword("");
		}
		new PostgresqlDatabaseClientInitializer(properties).initialize(context);
	}
}
