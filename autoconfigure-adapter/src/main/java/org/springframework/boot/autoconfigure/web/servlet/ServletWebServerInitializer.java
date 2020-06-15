package org.springframework.boot.autoconfigure.web.servlet;

import java.util.List;
import java.util.function.Supplier;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.function.support.HandlerFunctionAdapter;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.UrlPathHelper;

import static org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.EnableWebMvcConfiguration;
import static org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.ResourceChainCustomizerConfiguration;
import static org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.ResourceChainResourceHandlerRegistrationCustomizer;
import static org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.ResourceHandlerRegistrationCustomizer;
import static org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;

public class ServletWebServerInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final ServerProperties serverProperties;

	private final WebMvcProperties webMvcProperties;

	private final ResourceProperties resourceProperties;


	public ServletWebServerInitializer(ServerProperties serverProperties, WebMvcProperties webMvcProperties, ResourceProperties resourceProperties) {
		this.serverProperties = serverProperties;
		this.webMvcProperties = webMvcProperties;
		this.resourceProperties = resourceProperties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean("webServerFactoryCustomizerBeanPostProcessor", WebServerFactoryCustomizerBeanPostProcessor.class, WebServerFactoryCustomizerBeanPostProcessor::new);
		context.registerBean(WebMvcProperties.class, () -> this.webMvcProperties);
		context.registerBean(ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class, ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar::new);
		context.registerBean(TomcatServletWebServerFactory.class, () -> new ServletWebServerFactoryConfiguration.EmbeddedTomcat().tomcatServletWebServerFactory(
				context.getBeanProvider(TomcatConnectorCustomizer.class),
				context.getBeanProvider(TomcatContextCustomizer.class),
				context.getBeanProvider(ResolvableType.forClass(TomcatProtocolHandlerCustomizer.class))));
		ServletWebServerFactoryAutoConfiguration servletWebServerFactoryConfiguration = new ServletWebServerFactoryAutoConfiguration();
		context.registerBean(ServletWebServerFactoryCustomizer.class, () -> servletWebServerFactoryConfiguration.servletWebServerFactoryCustomizer(serverProperties));
		context.registerBean(TomcatServletWebServerFactoryCustomizer.class, () -> servletWebServerFactoryConfiguration.tomcatServletWebServerFactoryCustomizer(serverProperties));
		context.registerBean(FilterRegistrationBean.class, servletWebServerFactoryConfiguration::forwardedHeaderFilter);

		DispatcherServletAutoConfiguration.DispatcherServletConfiguration dispatcherServletConfiguration = new DispatcherServletAutoConfiguration.DispatcherServletConfiguration();
		context.registerBean(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME, DispatcherServlet.class, () -> dispatcherServletConfiguration.dispatcherServlet(webMvcProperties));
		context.registerBean(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME, DispatcherServletRegistrationBean.class, () -> new DispatcherServletAutoConfiguration.DispatcherServletRegistrationConfiguration().dispatcherServletRegistration(context.getBean(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME, DispatcherServlet.class), webMvcProperties, context.getBeanProvider(MultipartConfigElement.class)));

		WebMvcAutoConfiguration webMvcConfiguration = new WebMvcAutoConfiguration();
		context.registerBean(OrderedHiddenHttpMethodFilter.class, webMvcConfiguration::hiddenHttpMethodFilter);

		Supplier<WebMvcAutoConfigurationAdapter> webMvcConfigurationAdapter = new Supplier<WebMvcAutoConfigurationAdapter>() {

			private WebMvcAutoConfigurationAdapter configuration;

			@Override
			public WebMvcAutoConfigurationAdapter get() {
				if (configuration == null) {
					configuration = new WebMvcAutoConfigurationAdapter(resourceProperties, webMvcProperties, context, context.getBeanProvider(HttpMessageConverters.class), context.getBeanProvider(ResourceHandlerRegistrationCustomizer.class), context.getBeanProvider(DispatcherServletPath.class));
					return configuration;
				}
				return configuration;
			}
		};
		context.registerBean(InternalResourceViewResolver.class, () -> webMvcConfigurationAdapter.get().defaultViewResolver());
		context.registerBean(BeanNameViewResolver.class, () -> webMvcConfigurationAdapter.get().beanNameViewResolver());
		context.registerBean("viewResolver", ContentNegotiatingViewResolver.class, () -> webMvcConfigurationAdapter.get().viewResolver(context));
		context.registerBean(LocaleResolver.class, () -> webMvcConfigurationAdapter.get().localeResolver());
		context.registerBean(RequestContextFilter.class, WebMvcAutoConfigurationAdapter::requestContextFilter);
		// TODO Favicon management

		Supplier<EnableWebMvcConfiguration> enableWebMvcConfiguration = new Supplier<EnableWebMvcConfiguration>() {

			private EnableWebMvcConfiguration configuration;

			@Override
			public EnableWebMvcConfiguration get() {
				if (configuration == null) {
					configuration = new EnableWebMvcConfigurationWrapper(context.getBeanProvider(WebMvcProperties.class), context.getBeanProvider(WebMvcRegistrations.class), context);
					configuration.setApplicationContext(context);
					configuration.setServletContext(((WebApplicationContext) context).getServletContext());
					configuration.setResourceLoader(context);
				}
				return configuration;
			}
		};

		context.registerBean(FormattingConversionService.class, () -> enableWebMvcConfiguration.get().mvcConversionService());
		context.registerBean(Validator.class, () -> enableWebMvcConfiguration.get().mvcValidator());
		context.registerBean(ContentNegotiationManager.class, () -> enableWebMvcConfiguration.get().mvcContentNegotiationManager());
		context.registerBean(ResourceChainResourceHandlerRegistrationCustomizer.class, () -> new ResourceChainCustomizerConfiguration().resourceHandlerRegistrationCustomizer());
		context.registerBean(PathMatcher.class, () -> enableWebMvcConfiguration.get().mvcPathMatcher());
		context.registerBean(UrlPathHelper.class, () -> enableWebMvcConfiguration.get().mvcUrlPathHelper());
		context.registerBean(HandlerMapping.class, () ->  enableWebMvcConfiguration.get().viewControllerHandlerMapping(context.getBean(PathMatcher.class), context.getBean(UrlPathHelper.class), context.getBean(FormattingConversionService.class), context.getBean(ResourceUrlProvider.class)));
		context.registerBean(BeanNameUrlHandlerMapping.class, () ->  enableWebMvcConfiguration.get().beanNameHandlerMapping(context.getBean(FormattingConversionService.class), context.getBean(ResourceUrlProvider.class)));
		context.registerBean(RouterFunctionMapping.class, () ->  enableWebMvcConfiguration.get().routerFunctionMapping(context.getBean(FormattingConversionService.class), context.getBean(ResourceUrlProvider.class)));
		context.registerBean(HandlerMapping.class, () -> enableWebMvcConfiguration.get().resourceHandlerMapping(context.getBean(UrlPathHelper.class), context.getBean(PathMatcher.class), context.getBean(ContentNegotiationManager.class), context.getBean(FormattingConversionService.class), context.getBean(ResourceUrlProvider.class)));
		context.registerBean(ResourceUrlProvider.class, () -> enableWebMvcConfiguration.get().mvcResourceUrlProvider());
		context.registerBean(HandlerMapping.class, () -> enableWebMvcConfiguration.get().defaultServletHandlerMapping());
		context.registerBean(HandlerFunctionAdapter.class, () -> enableWebMvcConfiguration.get().handlerFunctionAdapter());
		context.registerBean(HttpRequestHandlerAdapter.class, () -> enableWebMvcConfiguration.get().httpRequestHandlerAdapter());
		context.registerBean(SimpleControllerHandlerAdapter.class, () -> enableWebMvcConfiguration.get().simpleControllerHandlerAdapter());
		context.registerBean(HandlerExceptionResolver.class, () -> enableWebMvcConfiguration.get().handlerExceptionResolver(context.getBean(ContentNegotiationManager.class)));
		context.registerBean(ViewResolver.class, () -> enableWebMvcConfiguration.get().mvcViewResolver(context.getBean(ContentNegotiationManager.class)));
		context.registerBean(HandlerMappingIntrospector.class, () -> enableWebMvcConfiguration.get().mvcHandlerMappingIntrospector(), bd -> bd.setLazyInit(true));
		context.registerBean(WelcomePageHandlerMapping.class, () -> enableWebMvcConfiguration.get().welcomePageHandlerMapping(context, context.getBean(FormattingConversionService.class), context.getBean(ResourceUrlProvider.class)));
	}

	private class EnableWebMvcConfigurationWrapper extends EnableWebMvcConfiguration {

		public EnableWebMvcConfigurationWrapper(ObjectProvider<WebMvcProperties> mvcPropertiesProvider, ObjectProvider<WebMvcRegistrations> mvcRegistrationsProvider, ListableBeanFactory beanFactory) {
			super(resourceProperties, mvcPropertiesProvider, mvcRegistrationsProvider, beanFactory);
		}

		@Override
		protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			getApplicationContext().getBeanProvider(HttpMessageConverter.class).orderedStream().forEach(converters::add);
		}

		@Override
		protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
			exceptionResolvers.add(new DefaultHandlerExceptionResolver());
		}

	}
}
