package org.springframework.boot.autoconfigure.mongo;

import java.util.List;

import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;

/**
* {@link ApplicationContextInitializer} adapter for {@link MongoReactiveAutoConfiguration}.
*/
public class MongoReactiveInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private MongoProperties properties;

	private boolean embeddedServer;

	public MongoReactiveInitializer(MongoProperties properties, boolean embeddedServer) {
		this.properties = properties;
		this.embeddedServer = embeddedServer;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(MongoClientSettingsBuilderCustomizer.class, () -> new MongoReactiveAutoConfiguration.NettyDriverConfiguration().nettyDriverCustomizer(context.getDefaultListableBeanFactory().getBeanProvider(MongoClientSettings.class)));

		context.registerBean(MongoClient.class, () -> {
			try {
				ObjectProvider<List<MongoClientSettingsBuilderCustomizer>> customizers = (ObjectProvider<List<MongoClientSettingsBuilderCustomizer>>)context.getDefaultListableBeanFactory().resolveDependency(new DependencyDescriptor(MethodParameter.forParameter(MongoReactiveAutoConfiguration.class.getDeclaredMethod("reactiveStreamsMongoClient", MongoProperties.class, Environment.class, ObjectProvider.class).getParameters()[2]), true), null);
				return new MongoReactiveAutoConfiguration(context.getBeanProvider(MongoClientSettings.class)).reactiveStreamsMongoClient(properties, context.getEnvironment(), customizers);
			}
			catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}, (definition) -> {
			if (embeddedServer) {
				definition.setDependsOn("embeddedMongoServer");
			}
		});
	}
}
