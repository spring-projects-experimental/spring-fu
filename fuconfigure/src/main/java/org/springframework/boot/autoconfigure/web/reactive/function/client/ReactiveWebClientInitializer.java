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

package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * {@link ApplicationContextInitializer} adapter for {@link WebClientAutoConfiguration}.
 */
public class ReactiveWebClientInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(WebClient.Builder.class, () -> {
			// TODO Fix when SPR-17272 will be fixed and Boot updated as well
			ObjectProvider<List<WebClientCustomizer>> customizers = (ObjectProvider<List<WebClientCustomizer>>)context.getDefaultListableBeanFactory().resolveDependency(new DependencyDescriptor(MethodParameter.forParameter(WebClientAutoConfiguration.class.getConstructors()[0].getParameters()[0]), true), null);
			return new WebClientAutoConfiguration(customizers).webClientBuilder();
		});
		context.registerBean(EmptyWebClientCodecCustomizer.class, () -> new EmptyWebClientCodecCustomizer(new ArrayList<>(context.getBeansOfType(CodecCustomizer.class).values())));
	}

	/**
	 * Variant of {@link WebClientCodecCustomizer} that configure empty default codecs by defaults
	 */
	static public class EmptyWebClientCodecCustomizer implements WebClientCustomizer {

		private final List<CodecCustomizer> codecCustomizers;

		public EmptyWebClientCodecCustomizer(List<CodecCustomizer> codecCustomizers) {
			this.codecCustomizers = codecCustomizers;
		}

		@Override
		public void customize(WebClient.Builder webClientBuilder) {
			webClientBuilder
					.exchangeStrategies(ExchangeStrategies.empty()
							.codecs((codecs) -> this.codecCustomizers
									.forEach((customizer) -> customizer.customize(codecs)))
							.build());
		}
	}
}
