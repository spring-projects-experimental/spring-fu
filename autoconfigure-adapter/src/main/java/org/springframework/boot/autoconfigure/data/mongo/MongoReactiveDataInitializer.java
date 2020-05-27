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

	private final MongoProperties properties;

	public MongoReactiveDataInitializer(MongoProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MongoReactiveDataAutoConfiguration configuration = new MongoReactiveDataAutoConfiguration();
		context.registerBean(MappingMongoConverter.class, () -> configuration.mappingMongoConverter(context.getBean(MongoMappingContext.class), context.getBean(MongoCustomConversions.class)));
		context.registerBean(SimpleReactiveMongoDatabaseFactory.class, () -> configuration.reactiveMongoDatabaseFactory(this.properties, context.getBean(MongoClient.class)));
		context.registerBean(ReactiveMongoTemplate.class, () -> configuration.reactiveMongoTemplate(context.getBean(ReactiveMongoDatabaseFactory.class), context.getBean(MongoConverter.class)));
	}
}
