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
import static org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.ResourceHandlerRegistrationCustomizer;
import static org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.WebFluxConfig;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.DependencyDescriptor;
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
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
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

		ReactiveWebServerFactoryAutoConfiguration configuration = new ReactiveWebServerFactoryAutoConfiguration();
		context.registerBean(ReactiveWebServerFactoryCustomizer.class, () -> configuration.reactiveWebServerFactoryCustomizer(this.serverProperties));
		context.registerBean(ConfigurableReactiveWebServerFactory.class, () -> serverFactory);
		context.registerBean(ErrorAttributes.class, () -> new DefaultErrorAttributes(serverProperties.getError().isIncludeException()));
		context.registerBean(ErrorWebExceptionHandler.class,  () -> {
			// TODO Fix when SPR-17272 will be fixed and Boot updated as well
			ObjectProvider<List<ViewResolver>> viewResolvers = (ObjectProvider<List<ViewResolver>>)context.getDefaultListableBeanFactory().resolveDependency(new DependencyDescriptor(MethodParameter.forParameter(ErrorWebFluxAutoConfiguration.class.getConstructors()[0].getParameters()[2]), true), null);
			ErrorWebFluxAutoConfiguration errorConfiguration = new ErrorWebFluxAutoConfiguration(this.serverProperties, this.resourceProperties, viewResolvers, context.getBean(ServerCodecConfigurer.class), context);
			return errorConfiguration.errorWebExceptionHandler(context.getBean(ErrorAttributes.class));
		});
		context.registerBean(EnableWebFluxConfigurationWrapper.class, () -> new EnableWebFluxConfigurationWrapper(context, webFluxProperties));
		context.registerBean(WebHttpHandlerBuilder.LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).localeContextResolver());
		context.registerBean(WebExceptionHandler.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).responseStatusExceptionHandler());
		context.registerBean(RouterFunctionMapping.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).routerFunctionMapping());
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
		context.registerBean(WebHttpHandlerBuilder.WEB_HANDLER_BEAN_NAME, DispatcherHandler.class, () -> context.getBean(EnableWebFluxConfigurationWrapper.class).webHandler());

		context.registerBean(WebFluxConfig.class, () -> {
			// TODO Fix when SPR-17272 will be fixed and Boot updated as well
			ObjectProvider<List<HandlerMethodArgumentResolver>> argmentResolvers = (ObjectProvider<List<HandlerMethodArgumentResolver>>)context.getDefaultListableBeanFactory().resolveDependency(new DependencyDescriptor(MethodParameter.forParameter(WebFluxConfig.class.getConstructors()[0].getParameters()[3]), true), null);
			ObjectProvider<List<CodecCustomizer>> codecCustomizers = (ObjectProvider<List<CodecCustomizer>>)context.getDefaultListableBeanFactory().resolveDependency(new DependencyDescriptor(MethodParameter.forParameter(WebFluxConfig.class.getConstructors()[0].getParameters()[4]), true), null);
			ObjectProvider<List<ViewResolver>> viewResolvers = (ObjectProvider<List<ViewResolver>>)context.getDefaultListableBeanFactory().resolveDependency(new DependencyDescriptor(MethodParameter.forParameter(WebFluxConfig.class.getConstructors()[0].getParameters()[6]), true), null);
			return new WebFluxConfig(resourceProperties, webFluxProperties, context, argmentResolvers, codecCustomizers,
				context.getBeanProvider(ResourceHandlerRegistrationCustomizer.class), viewResolvers);
		});


	}

	private class EnableWebFluxConfigurationWrapper extends EnableWebFluxConfiguration {

		public EnableWebFluxConfigurationWrapper(GenericApplicationContext context, WebFluxProperties webFluxProperties) {
			super(webFluxProperties, context.getBeanProvider(WebFluxRegistrations.class));
		}
	}
}
