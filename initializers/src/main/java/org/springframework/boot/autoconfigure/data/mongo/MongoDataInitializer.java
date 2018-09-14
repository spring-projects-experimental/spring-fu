package org.springframework.boot.autoconfigure.data.mongo;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * {@link ApplicationContextInitializer} adapter for {@link MongoDataConfiguration}.
 */
public class MongoDataInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private MongoProperties properties;

	public MongoDataInitializer(MongoProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MongoDataConfiguration dataConfiguration = new MongoDataConfiguration(context, this.properties);
		context.registerBean(MongoMappingContext.class, () -> {
			try {
				return dataConfiguration.mongoMappingContext(context.getBean(MongoCustomConversions.class));
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		});
		context.registerBean(MongoCustomConversions.class, dataConfiguration::mongoCustomConversions);
	}
}
