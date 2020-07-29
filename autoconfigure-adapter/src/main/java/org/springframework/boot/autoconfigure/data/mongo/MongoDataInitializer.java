/*
 * Copyright 2002-2018 the original author or authors.
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

	private final MongoProperties properties;

	public MongoDataInitializer(MongoProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MongoDataConfiguration dataConfiguration = new MongoDataConfiguration();
		context.registerBean(MongoCustomConversions.class, dataConfiguration::mongoCustomConversions);
		context.registerBean(MongoMappingContext.class, () -> {
			try {
				return dataConfiguration.mongoMappingContext(context, properties, context.getBean(MongoCustomConversions.class));
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		});
		context.registerBean(MongoCustomConversions.class, dataConfiguration::mongoCustomConversions);
	}
}
