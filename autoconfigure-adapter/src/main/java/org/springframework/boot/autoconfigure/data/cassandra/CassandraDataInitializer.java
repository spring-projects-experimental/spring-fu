package org.springframework.boot.autoconfigure.data.cassandra;

import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;

import java.util.function.Supplier;

import com.datastax.oss.driver.api.core.CqlSession;

/**
 * {@link ApplicationContextInitializer} adapter for {@link CassandraReactiveDataAutoConfiguration}.
 */
public class CassandraDataInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final CassandraProperties properties;

	public CassandraDataInitializer(CassandraProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		Supplier<CassandraDataAutoConfiguration> configurationSupplier = () -> new CassandraDataAutoConfiguration(context.getBean(CqlSession.class));

		context.registerBean(CassandraCustomConversions.class, () -> configurationSupplier.get().cassandraCustomConversions());
		context.registerBean(CassandraMappingContext.class, () -> getCassandraMappingContext(context, configurationSupplier));
		context.registerBean(CassandraConverter.class, () -> configurationSupplier.get().cassandraConverter(context.getBean(CassandraMappingContext.class), context.getBean(CassandraCustomConversions.class)));
		context.registerBean(SessionFactoryFactoryBean.class, () -> getCassandraSessionFactoryBean(context, configurationSupplier));
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
			return configurationSupplier.get().cassandraTemplate(context.getBean(SessionFactory.class), context.getBean(CassandraConverter.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private SessionFactoryFactoryBean getCassandraSessionFactoryBean(GenericApplicationContext context, Supplier<CassandraDataAutoConfiguration> configurationSupplier) {
		try {
			return configurationSupplier.get().cassandraSessionFactory(context.getEnvironment(), context.getBean(CassandraConverter.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
