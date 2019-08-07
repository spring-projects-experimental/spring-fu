package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;

import java.util.function.Supplier;

/**
 * {@link ApplicationContextInitializer} adapter for {@link CassandraReactiveDataAutoConfiguration}.
 */
public class CassandraDataInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private CassandraProperties properties;

	public CassandraDataInitializer(CassandraProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		Supplier<CassandraDataAutoConfiguration> configurationSupplier = () -> new CassandraDataAutoConfiguration(properties, context.getBean(Cluster.class));

		context.registerBean(CassandraCustomConversions.class, () -> configurationSupplier.get().cassandraCustomConversions());
		context.registerBean(CassandraMappingContext.class, () -> getCassandraMappingContext(context, configurationSupplier));
		context.registerBean(CassandraConverter.class, () -> configurationSupplier.get().cassandraConverter(context.getBean(CassandraMappingContext.class), context.getBean(CassandraCustomConversions.class)));
		context.registerBean(CassandraSessionFactoryBean.class, () -> getCassandraSessionFactoryBean(context, configurationSupplier));
		context.registerBean(CassandraTemplate.class, () -> getCassandraTemplate(context, configurationSupplier));
	}

	private CassandraMappingContext getCassandraMappingContext(GenericApplicationContext context, Supplier<CassandraDataAutoConfiguration> configurationSupplier) {
		try {
			return configurationSupplier.get().cassandraMapping(context.getBeanFactory(), context.getBean(CassandraCustomConversions.class));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private CassandraTemplate getCassandraTemplate(GenericApplicationContext context, Supplier<CassandraDataAutoConfiguration> configurationSupplier) {
		try {
			return configurationSupplier.get().cassandraTemplate(context.getBean(Session.class), context.getBean(CassandraConverter.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private CassandraSessionFactoryBean getCassandraSessionFactoryBean(GenericApplicationContext context, Supplier<CassandraDataAutoConfiguration> configurationSupplier) {
		try {
			return configurationSupplier.get().cassandraSession(context.getEnvironment(), context.getBean(CassandraConverter.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
