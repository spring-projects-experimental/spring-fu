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

package org.springframework.boot.autoconfigure.web.reactive;

import static org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.EnableWebFluxConfiguration;
import static org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.WebFluxConfig;
import static org.springframework.web.server.adapter.WebHttpHandlerBuilder.*;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.format.support.*;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.validation.*;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.support.HandlerFunctionAdapter;
import org.springframework.web.reactive.function.server.support.RouterFunctionMapping;
import org.springframework.web.reactive.function.server.support.ServerResponseResultHandler;
import org.springframework.web.reactive.result.SimpleHandlerAdapter;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.view.ViewResolutionResultHandler;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.i18n.LocaleContextResolver;

/**
 * {@link ApplicationContextInitializer} adapter for Reactive webflux server auto-configurations.
 */
@SuppressWarnings("deprecation")
public class ReactiveWebServerInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final ServerProperties serverProperties;

	private final ResourceProperties resourceProperties;

	private final WebProperties webProperties;

	private final ConfigurableReactiveWebServerFactory serverFactory;

	private final WebFluxProperties webFluxProperties;


	public ReactiveWebServerInitializer(ServerProperties serverProperties, ResourceProperties resourceProperties, WebProperties webProperties, WebFluxProperties webFluxProperties, ConfigurableReactiveWebServerFactory serverFactory) {
		this.serverProperties = serverProperties;
		this.resourceProperties = resourceProperties;
		this.webProperties = webProperties;
		this.webFluxProperties = webFluxProperties;
		this.serverFactory = serverFactory;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean("webServerFactoryCustomizerBeanPostProcessor", WebServerFactoryCustomizerBeanPostProcessor.class, WebServerFactoryCustomizerBeanPostProcessor::new);

		context.registerBean(ReactiveWebServerFactoryCustomizer.class, () -> new ReactiveWebServerFactoryCustomizer(this.serverProperties));
		context.registerBean(ConfigurableReactiveWebServerFactory.class, () -> serverFactory);
		context.registerBean(ErrorAttributes.class, () -> new DefaultErrorAttributes(serverProperties.getError().isIncludeException()));
		context.registerBean(ErrorWebExceptionHandler.class,  () -> {
			ErrorWebFluxAutoConfiguration errorConfiguration = new ErrorWebFluxAutoConfiguration(this.serverProperties);
			return errorConfiguration.errorWebExceptionHandler(context.getBean(ErrorAttributes.class), this.resourceProperties, this.webProperties, context.getBeanProvider(ViewResolver.class), context.getBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class), context);
		});
		context.registerBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class, () -> new EnableWebFluxConfigurationWrapper(context, webFluxProperties, webProperties));
		context.registerBean(LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).localeContextResolver());
		context.registerBean("responseStatusExceptionHandler", WebExceptionHandler.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).responseStatusExceptionHandler());

		context.registerBean(RouterFunctionMapping.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).routerFunctionMapping(context.getBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class)));
		context.registerBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).serverCodecConfigurer());
		context.registerBean("webFluxAdapterRegistry", ReactiveAdapterRegistry.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).webFluxAdapterRegistry());
		context.registerBean("handlerFunctionAdapter", HandlerFunctionAdapter.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).handlerFunctionAdapter());
		context.registerBean("webFluxContentTypeResolver", RequestedContentTypeResolver.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).webFluxContentTypeResolver());
		context.registerBean("webFluxConversionService", FormattingConversionService.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).webFluxConversionService());
		context.registerBean("serverResponseResultHandler", ServerResponseResultHandler.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).serverResponseResultHandler(context.getBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class)));
		context.registerBean("simpleHandlerAdapter", SimpleHandlerAdapter.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).simpleHandlerAdapter());
		context.registerBean("viewResolutionResultHandler", ViewResolutionResultHandler.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).viewResolutionResultHandler(context.getBean("webFluxAdapterRegistry", ReactiveAdapterRegistry.class), context.getBean("webFluxContentTypeResolver", RequestedContentTypeResolver.class)));
		context.registerBean("webFluxValidator", Validator.class, () -> context.getBean("fuWebFluxConfiguration", EnableWebFluxConfigurationWrapper.class).webFluxValidator());
		context.registerBean(HttpHandler.class, () -> applicationContext(context).build());
		context.registerBean(WEB_HANDLER_BEAN_NAME, DispatcherHandler.class, (Supplier<DispatcherHandler>) DispatcherHandler::new);
		context.registerBean(WebFluxConfig.class, () -> new WebFluxConfig(resourceProperties, webProperties, webFluxProperties, context, context.getBeanProvider(HandlerMethodArgumentResolver.class), context.getBeanProvider(CodecCustomizer.class),
			context.getBeanProvider(ResourceHandlerRegistrationCustomizer.class), context.getBeanProvider(ViewResolver.class)));
	}

	private class EnableWebFluxConfigurationWrapper extends EnableWebFluxConfiguration {

		public EnableWebFluxConfigurationWrapper(GenericApplicationContext context, WebFluxProperties webFluxProperties, WebProperties webProperties) {
			super(webFluxProperties, webProperties, context.getBeanProvider(WebFluxRegistrations.class));
			setConfigurers(new ArrayList<>(context.getBeansOfType(WebFluxConfigurer.class).values()));
		}

		@Override
		public ServerCodecConfigurer serverCodecConfigurer() {
			ServerCodecConfigurer configurer = ServerCodecConfigurer.create();
			configurer.registerDefaults(false);
			getApplicationContext().getBeanProvider(CodecCustomizer.class)
					.forEach((customizer) -> customizer.customize(configurer));
			return configurer;
		}


	}

}
