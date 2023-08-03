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

import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * {@link ApplicationContextInitializer} adapter for {@link WebClientAutoConfiguration}.
 */
public class ReactiveWebClientBuilderInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final String baseUrl;

	public ReactiveWebClientBuilderInitializer(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(WebClient.Builder.class, () -> new WebClientAutoConfiguration().webClientBuilder(context.getBeanProvider(WebClientCustomizer.class)));
		context.registerBean(DefaultWebClientCodecCustomizer.class, () -> new DefaultWebClientCodecCustomizer(this.baseUrl, new ArrayList<>(context.getBeansOfType(CodecCustomizer.class).values())));
	}

	/**
	 * Variant of {@link WebClientCodecCustomizer} that configure empty default codecs by defaults
	 */
	static public class DefaultWebClientCodecCustomizer implements WebClientCustomizer {

		private final List<CodecCustomizer> codecCustomizers;

		private final String baseUrl;

		public DefaultWebClientCodecCustomizer(String baseUrl, List<CodecCustomizer> codecCustomizers) {
			this.codecCustomizers = codecCustomizers;
			this.baseUrl = baseUrl;
		}

		@Override
		public void customize(WebClient.Builder builder) {
			builder.exchangeStrategies(ExchangeStrategies.empty()
							.codecs(codecs -> this.codecCustomizers
									.forEach((customizer) -> customizer.customize(codecs))).build());
			if (this.baseUrl != null) {
				builder.baseUrl(this.baseUrl);
			}
		}
	}
}
