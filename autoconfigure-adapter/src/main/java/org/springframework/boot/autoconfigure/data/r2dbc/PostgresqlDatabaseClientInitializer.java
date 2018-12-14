package org.springframework.boot.autoconfigure.data.r2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.r2dbc.function.CoDatabaseClient;
import org.springframework.data.r2dbc.function.DatabaseClient;

public class PostgresqlDatabaseClientInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final PostgresqlR2dbcProperties properties;

	public PostgresqlDatabaseClientInitializer(PostgresqlR2dbcProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {

		PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration
				.builder()
				.host(this.properties.getHost())
				.port(this.properties.getPort())
				.database(this.properties.getDatabase())
				.username(this.properties.getUsername())
				.password(this.properties.getPassword())
				.build();

		if (properties.getCoroutines()) {
			context.registerBean(CoDatabaseClient.class, () -> CoDatabaseClient.Companion.create(new PostgresqlConnectionFactory(configuration)));
		}
		else {
			context.registerBean(DatabaseClient.class, () -> DatabaseClient.builder().connectionFactory(new PostgresqlConnectionFactory(configuration)).build());
		}
	}
}
