package org.springframework.boot.autoconfigure.r2dbc;

import java.util.List;

import io.r2dbc.spi.ConnectionFactory;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

public class R2dbcInitializer  implements ApplicationContextInitializer<GenericApplicationContext> {
	private final R2dbcProperties properties;
	private final List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers;
	private final boolean transactional;

	public R2dbcInitializer(R2dbcProperties properties, List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers, boolean transactional) {
		this.properties = properties;
		this.optionsCustomizers = optionsCustomizers;
		this.transactional = transactional;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		ConnectionFactory connectionFactory = ConnectionFactoryBuilder.of(properties, () -> EmbeddedDatabaseConnection.get(context.getClassLoader()))
				.configure((options) -> {
					for (ConnectionFactoryOptionsBuilderCustomizer optionsCustomizer : optionsCustomizers) {
						optionsCustomizer.customize(options);
					}
				})
				.build();

		context.registerBean(ConnectionFactory.class, () -> connectionFactory);
		context.registerBean(DatabaseClient.class, () -> DatabaseClient.builder().connectionFactory(connectionFactory).build());

		if (transactional) {
			ReactiveTransactionManager reactiveTransactionManager = new R2dbcTransactionManager(connectionFactory);
			context.registerBean(TransactionalOperator.class, () -> TransactionalOperator.create(reactiveTransactionManager));
		}
	}
}
