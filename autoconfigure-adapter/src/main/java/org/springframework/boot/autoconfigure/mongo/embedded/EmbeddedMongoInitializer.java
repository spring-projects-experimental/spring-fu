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

package org.springframework.boot.autoconfigure.mongo.embedded;

import java.io.IOException;

import de.flapdoodle.embed.mongo.MongodExecutable;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.process.config.RuntimeConfig;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public class EmbeddedMongoInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final MongoProperties properties;

	private final EmbeddedMongoProperties embeddedProperties;

	public EmbeddedMongoInitializer(MongoProperties mongoProperties, EmbeddedMongoProperties embeddedProperties) {
		this.properties = mongoProperties;
		this.embeddedProperties = embeddedProperties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		// , this.embeddedProperties
		EmbeddedMongoAutoConfiguration configuration = new EmbeddedMongoAutoConfiguration(this.properties);
		context.registerBean(MongodConfig.class, () -> {
			try {
				return configuration.embeddedMongoConfiguration(this.embeddedProperties);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});
		context.registerBean("embeddedMongoServer", MongodExecutable.class, () ->
				configuration.embeddedMongoServer(context.getBean(MongodConfig.class), context.getBean(RuntimeConfig.class), context), definition -> {
			definition.setInitMethodName("start");
			definition.setDestroyMethodName("stop");
		});
		context.registerBean(RuntimeConfig.class, () -> new EmbeddedMongoAutoConfiguration.RuntimeConfigConfiguration().embeddedMongoRuntimeConfig(context.getBeanProvider(DownloadConfigBuilderCustomizer.class)));
	}
}
