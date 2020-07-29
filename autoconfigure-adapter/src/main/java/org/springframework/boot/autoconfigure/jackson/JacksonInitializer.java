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

package org.springframework.boot.autoconfigure.jackson;

import static org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.Jackson2ObjectMapperBuilderCustomizerConfiguration;
import static org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.JacksonObjectMapperBuilderConfiguration;
import static org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.JacksonObjectMapperConfiguration;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * {@link ApplicationContextInitializer} adapter for {@link JacksonAutoConfiguration}.
 */
public class JacksonInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final JacksonProperties properties;

	public JacksonInitializer(JacksonProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(Jackson2ObjectMapperBuilderCustomizer.class, () -> new Jackson2ObjectMapperBuilderCustomizerConfiguration().standardJacksonObjectMapperBuilderCustomizer(context, this.properties));
		JacksonObjectMapperBuilderConfiguration configuration = new JacksonObjectMapperBuilderConfiguration();
		context.registerBean(Jackson2ObjectMapperBuilder.class, () ->
				configuration.jacksonObjectMapperBuilder(context, new ArrayList<>(context.getBeansOfType(Jackson2ObjectMapperBuilderCustomizer.class).values())));
		context.registerBean(ObjectMapper.class, () -> new JacksonObjectMapperConfiguration().jacksonObjectMapper(context.getBean(Jackson2ObjectMapperBuilder.class)));
	}
}
