package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

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
		CassandraAutoConfiguration configuration = new CassandraAutoConfiguration();
		context.registerBean(CqlSession.class, () -> configuration.cassandraSession(context.getBean(CqlSessionBuilder.class)));
		context.registerBean(CqlSessionBuilder.class, () -> configuration.cassandraSessionBuilder(properties, context.getBean(DriverConfigLoader.class), context.getBeanProvider(CqlSessionBuilderCustomizer.class)));
		context.registerBean(DriverConfigLoader.class, () -> configuration.cassandraDriverConfigLoader(properties, context.getBeanProvider(DriverConfigLoaderBuilderCustomizer.class)));
	}
}
