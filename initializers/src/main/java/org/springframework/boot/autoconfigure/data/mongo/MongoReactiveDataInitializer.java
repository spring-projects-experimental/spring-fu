package org.springframework.boot.autoconfigure.data.mongo;

import com.mongodb.reactivestreams.client.MongoClient;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * {@link ApplicationContextInitializer} adapter for {@link MongoReactiveDataAutoConfiguration}.
 */
public class MongoReactiveDataInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private MongoProperties properties;

	public MongoReactiveDataInitializer(MongoProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MongoReactiveDataAutoConfiguration configuration = new MongoReactiveDataAutoConfiguration(this.properties);
		context.registerBean(MappingMongoConverter.class, () -> configuration.mappingMongoConverter(context.getBean(MongoMappingContext.class), context.getBean(MongoCustomConversions.class)));
		context.registerBean(SimpleReactiveMongoDatabaseFactory.class, () -> configuration.reactiveMongoDatabaseFactory(context.getBean(MongoClient.class)));
		context.registerBean(ReactiveMongoTemplate.class, () -> configuration.reactiveMongoTemplate(context.getBean(ReactiveMongoDatabaseFactory.class), context.getBean(MongoConverter.class)));
	}
}
