/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;

import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * {@link ApplicationContextInitializer} adapter for {@link MongoAutoConfiguration}.
 */
public class MongoInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final MongoProperties properties;

	private final boolean embeddedServer;

	public MongoInitializer(MongoProperties properties, boolean embeddedServer) {
		this.properties = properties;
		this.embeddedServer = embeddedServer;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MongoAutoConfiguration configuration = new MongoAutoConfiguration();
		MongoAutoConfiguration.MongoClientSettingsConfiguration mongoClientSettingsConfiguration = new MongoAutoConfiguration.MongoClientSettingsConfiguration();
		context.registerBean(MongoClientSettingsBuilderCustomizer.class, () -> mongoClientSettingsConfiguration.mongoPropertiesCustomizer(this.properties, context.getEnvironment()));
		context.registerBean(MongoClient.class, () -> configuration.mongo(context.getBeanProvider(MongoClientSettingsBuilderCustomizer.class), mongoClientSettingsConfiguration.mongoClientSettings()), (definition) -> {
			if (embeddedServer) {
				definition.setDependsOn("embeddedMongoServer");
			}
		});
	}
}
