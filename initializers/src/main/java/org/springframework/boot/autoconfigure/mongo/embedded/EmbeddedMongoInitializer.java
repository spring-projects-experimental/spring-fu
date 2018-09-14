package org.springframework.boot.autoconfigure.mongo.embedded;

import java.io.IOException;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.process.config.IRuntimeConfig;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public class EmbeddedMongoInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private MongoProperties properties;

	private EmbeddedMongoProperties embeddedProperties;

	public EmbeddedMongoInitializer(MongoProperties mongoProperties, EmbeddedMongoProperties embeddedProperties) {
		this.properties = mongoProperties;
		this.embeddedProperties = embeddedProperties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		IRuntimeConfig config = new EmbeddedMongoAutoConfiguration.RuntimeConfigConfiguration().embeddedMongoRuntimeConfig();
		EmbeddedMongoAutoConfiguration configuration = new EmbeddedMongoAutoConfiguration(this.properties, this.embeddedProperties, context, config);
		context.registerBean(IMongodConfig.class, () -> {
			try {
				return configuration.embeddedMongoConfiguration();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});
		context.registerBean("embeddedMongoServer", MongodExecutable.class, () -> {
			try {
				return configuration.embeddedMongoServer(context.getBean(IMongodConfig.class));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}, definition -> {
			definition.setInitMethodName("start");
			definition.setDestroyMethodName("stop");
		});
		context.registerBean(IRuntimeConfig.class, () -> config);
	}
}
