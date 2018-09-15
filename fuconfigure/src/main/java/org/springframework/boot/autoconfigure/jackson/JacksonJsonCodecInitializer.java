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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

/**
 * {@link ApplicationContextInitializer} adapter for registering Jackson JSON codecs.
 */
public class JacksonJsonCodecInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(BeanPostProcessor.class, () -> new BeanPostProcessor() {

			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof CodecConfigurer) {
					CodecConfigurer.CustomCodecs codecs = ((CodecConfigurer)bean).customCodecs();
					ObjectMapper mapper = context.getBean(ObjectMapper.class);
					Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(mapper);
					codecs.decoder(new Jackson2JsonDecoder(mapper));
					codecs.encoder(encoder);
					codecs.writer(new ServerSentEventHttpMessageWriter(encoder));
				}
				return bean;
			}
		});
	}
}
