package org.springframework.boot.autoconfigure.data.r2dbc;

import io.r2dbc.mssql.MssqlConnectionConfiguration;
import io.r2dbc.mssql.MssqlConnectionFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.r2dbc.core.DatabaseClient;

public class MssqlDatabaseClientInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final MssqlR2dbcProperties properties;

	public MssqlDatabaseClientInitializer(MssqlR2dbcProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {

		MssqlConnectionConfiguration.Builder builder = MssqlConnectionConfiguration
				.builder()
				.host(this.properties.getHost())
				.port(this.properties.getPort())
				.database(this.properties.getDatabase())
				.username(this.properties.getUsername())
				.password(this.properties.getPassword())
				.connectTimeout(this.properties.getConnectTimeout())
				.preferCursoredExecution(this.properties.isPreferCursoredExecution());

		if (this.properties.isSsl()) {
			builder.enableSsl();
		}

		MssqlConnectionConfiguration configuration = builder.build();

		context.registerBean(DatabaseClient.class, () -> DatabaseClient.builder().connectionFactory(new MssqlConnectionFactory(configuration)).build());
	}
}
