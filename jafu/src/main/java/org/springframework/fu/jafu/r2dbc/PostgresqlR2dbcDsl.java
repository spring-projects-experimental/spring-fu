package org.springframework.fu.jafu.r2dbc;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlDatabaseClientInitializer;
import org.springframework.boot.autoconfigure.data.r2dbc.PostgresqlR2dbcProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for R2DBC configuration.
 * @author Sebastien Deleuze
 */
public class PostgresqlR2dbcDsl extends AbstractDsl {

	private final Consumer<PostgresqlR2dbcDsl> dsl;

	private final PostgresqlR2dbcProperties properties = new PostgresqlR2dbcProperties();

	private PostgresqlR2dbcDsl(Consumer<PostgresqlR2dbcDsl> dsl) {
		this.dsl = dsl;
	}

	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcPostgresql() {
		return new PostgresqlR2dbcDsl(mongoDsl -> {});
	}

	public static ApplicationContextInitializer<GenericApplicationContext> r2dbcPostgresql(Consumer<PostgresqlR2dbcDsl> dsl) {
		return new PostgresqlR2dbcDsl(dsl);
	}

	public PostgresqlR2dbcDsl host(String host) {
		properties.setHost(host);
		return this;
	}

	public PostgresqlR2dbcDsl port(Integer port) {
		properties.setPort(port);
		return this;
	}

	public PostgresqlR2dbcDsl database(String database) {
		properties.setDatabase(database);
		return this;
	}

	public PostgresqlR2dbcDsl username(String username) {
		properties.setUsername(username);
		return this;
	}

	public PostgresqlR2dbcDsl password(String password) {
		properties.setPassword(password);
		return this;
	}

	@Override
	public void register() {
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
