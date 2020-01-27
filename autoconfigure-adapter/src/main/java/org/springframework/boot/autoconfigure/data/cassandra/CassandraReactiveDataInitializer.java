package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.cassandra.ReactiveSession;
import org.springframework.data.cassandra.ReactiveSessionFactory;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;

/**
 * {@link ApplicationContextInitializer} adapter for {@link CassandraReactiveDataAutoConfiguration}.
 */
public class CassandraReactiveDataInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		CassandraReactiveDataAutoConfiguration configuration = new CassandraReactiveDataAutoConfiguration();

		context.registerBean(ReactiveSession.class, () -> configuration.reactiveCassandraSession(context.getBean(CqlSession.class)));
		context.registerBean(ReactiveSessionFactory.class, () -> configuration.reactiveCassandraSessionFactory(context.getBean(ReactiveSession.class)));
		context.registerBean(ReactiveCassandraTemplate.class, () -> configuration.reactiveCassandraTemplate(context.getBean(ReactiveSession.class), context.getBean(CassandraConverter.class)));
	}
}
