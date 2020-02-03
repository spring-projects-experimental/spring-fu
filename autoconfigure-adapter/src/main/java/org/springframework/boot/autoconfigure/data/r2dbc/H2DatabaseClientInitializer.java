package org.springframework.boot.autoconfigure.data.r2dbc;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.r2dbc.core.DatabaseClient;

public class H2DatabaseClientInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final H2R2dbcProperties properties;

	public H2DatabaseClientInitializer(H2R2dbcProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {

		H2ConnectionConfiguration configuration = H2ConnectionConfiguration
				.builder()
				.url(this.properties.getUrl())
				.username(this.properties.getUsername())
				.password(this.properties.getPassword())
				.build();

		context.registerBean(ConnectionFactory.class, () -> new H2ConnectionFactory(configuration));
		context.registerBean(DatabaseClient.class, () -> DatabaseClient.builder().connectionFactory(context.getBean(ConnectionFactory.class)).build());
	}
}
