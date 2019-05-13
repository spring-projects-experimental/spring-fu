package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.driver.core.Cluster;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * {@link ApplicationContextInitializer} adapter for {@link CassandraAutoConfiguration}.
 */
public class CassandraInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private CassandraProperties properties;

	public CassandraInitializer(CassandraProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		CassandraAutoConfiguration configuration = new CassandraAutoConfiguration();
		context.registerBean(Cluster.class, () -> configuration.cassandraCluster(properties, context.getBeanProvider(ClusterBuilderCustomizer.class), context.getBeanProvider(ClusterFactory.class)));
	}
}
