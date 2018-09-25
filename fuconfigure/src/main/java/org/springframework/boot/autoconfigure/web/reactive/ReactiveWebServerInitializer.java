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

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
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
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.support.HandlerFunctionAdapter;
import org.springframework.web.reactive.function.server.support.RouterFunctionMapping;
import org.springframework.web.reactive.function.server.support.ServerResponseResultHandler;
import org.springframework.web.reactive.result.SimpleHandlerAdapter;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler;
import org.springframework.web.reactive.result.view.ViewResolutionResultHandler;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.server.i18n.LocaleContextResolver;

/**
 * {@link ApplicationContextInitializer} adapter for Reactive web server auto-configurations.
 */
public class ReactiveWebServerInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private ServerProperties serverProperties;

	private ResourceProperties resourceProperties;

	private ConfigurableReactiveWebServerFactory serverFactory;

	private WebFluxProperties webFluxProperties;


	public ReactiveWebServerInitializer(ServerProperties serverProperties, ResourceProperties resourceProperties, WebFluxProperties webFluxProperties, ConfigurableReactiveWebServerFactory serverFactory) {
		this.serverProperties = serverProperties;
		this.resourceProperties = resourceProperties;
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
			ErrorWebFluxAutoConfiguration errorConfiguration = new ErrorWebFluxAutoConfiguration(this.serverProperties, this.resourceProperties, context.getBeanProvider(ViewResolver.class), context.getBean(ServerCodecConfigurer.class), context);
			return errorConfiguration.errorWebExceptionHandler(context.getBean(ErrorAttributes.class));
		});
		context.registerBean(EnableWebFluxConfigurationWrapper.class, () -> new EnableWebFluxConfigurationWrapper(context, webFluxProperties));
		context.registerBean(WebHttpHandlerBuilder.LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).localeContextResolver());
		context.registerBean(WebExceptionHandler.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).responseStatusExceptionHandler());

		// TODO See with Arjen if we can avoid the SortedRouterFunctionsContainer trick, seems to not be GraalVM friendly
		context.registerBean(RouterFunctionMapping.class, () -> {
			@SuppressWarnings("unchecked")
			RouterFunction<ServerResponse> router = context.getBeansOfType(RouterFunction.class).values().stream().reduce(RouterFunction::andOther).orElse(null);
			RouterFunctionMapping mapping = new RouterFunctionMapping(router);
			mapping.setOrder(-1);
			mapping.setMessageReaders(((ServerCodecConfigurer)context.getBean(WebHttpHandlerBuilder.SERVER_CODEC_CONFIGURER_BEAN_NAME)).getReaders());
			return mapping;
		});

		context.registerBean(WebHttpHandlerBuilder.SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).serverCodecConfigurer());
		context.registerBean(ReactiveAdapterRegistry.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).webFluxAdapterRegistry());
		context.registerBean(HandlerFunctionAdapter.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).handlerFunctionAdapter());
		context.registerBean(ResponseBodyResultHandler.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).responseBodyResultHandler());
		context.registerBean(ResponseEntityResultHandler.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).responseEntityResultHandler());
		context.registerBean(RequestedContentTypeResolver.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).webFluxContentTypeResolver());
		context.registerBean(RequestMappingHandlerAdapter.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).requestMappingHandlerAdapter());
		context.registerBean(ServerResponseResultHandler.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).serverResponseResultHandler());
		context.registerBean(SimpleHandlerAdapter.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).simpleHandlerAdapter());
		context.registerBean(ViewResolutionResultHandler.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).viewResolutionResultHandler());
		context.registerBean(HttpHandler.class, () -> WebHttpHandlerBuilder.applicationContext(context).build());
		context.registerBean(WebHttpHandlerBuilder.WEB_HANDLER_BEAN_NAME, DispatcherHandler.class, () -> new DispatcherHandler());

		context.registerBean(WebFluxConfig.class, () -> new WebFluxConfig(resourceProperties, webFluxProperties, context, context.getBeanProvider(HandlerMethodArgumentResolver.class), context.getBeanProvider(CodecCustomizer.class),
			context.getBeanProvider(ResourceHandlerRegistrationCustomizer.class), context.getBeanProvider(ViewResolver.class)));


	}

	private class EnableWebFluxConfigurationWrapper extends EnableWebFluxConfiguration {

		public EnableWebFluxConfigurationWrapper(GenericApplicationContext context, WebFluxProperties webFluxProperties) {
			super(webFluxProperties, context.getBeanProvider(WebFluxRegistrations.class));
		}

		@Override
		public ServerCodecConfigurer serverCodecConfigurer() {
			ServerCodecConfigurer configurer = ServerCodecConfigurer.create();
			configurer.registerDefaults(true);
			getApplicationContext().getBeanProvider(CodecCustomizer.class)
					.forEach((customizer) -> customizer.customize(configurer));
			return configurer;
		}
	}

}
