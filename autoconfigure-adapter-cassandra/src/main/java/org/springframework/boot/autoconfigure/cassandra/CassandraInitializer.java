package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * {@link ApplicationContextInitializer} adapter for {@link CassandraAutoConfiguration}.
 */
public class CassandraInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final CassandraProperties properties;

	public CassandraInitializer(CassandraProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		CassandraAutoConfiguration configuration = new CassandraAutoConfiguration(properties);

		context.registerBean(
			CqlSession.class,
			() -> configuration.cassandraSession(context.getBean(CqlSessionBuilder.class))
		);

		context.registerBean(
			CassandraConnectionDetails.class,
			configuration::cassandraConnectionDetails
		);

		context.registerBean(
			CqlSessionBuilder.class,
			() -> configuration.cassandraSessionBuilder(
				context.getBean(DriverConfigLoader.class),
				context.getBean(CassandraConnectionDetails.class),
				context.getBeanProvider(CqlSessionBuilderCustomizer.class),
				context.getBeanProvider(SslBundles.class)
			)
		);

		context.registerBean(
			DriverConfigLoader.class,
			() -> configuration.cassandraDriverConfigLoader(
				context.getBean(CassandraConnectionDetails.class),
				context.getBeanProvider(DriverConfigLoaderBuilderCustomizer.class)
			)
		);
	}
}
