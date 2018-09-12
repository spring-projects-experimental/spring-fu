package org.springframework.boot.autoconfigure.web.reactive

import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor
import org.springframework.context.support.registerBean
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory
import org.springframework.beans.factory.getBean
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.web.reactive.result.view.ViewResolver
import org.springframework.core.ResolvableType
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.EnableWebFluxConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.WebFluxConfig
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver

internal fun registerReactiveWebServerConfiguration(context: GenericApplicationContext, serverProperties: ServerProperties, resourceProperties: ResourceProperties, webFluxProperties: WebFluxProperties, serverFactory: ConfigurableReactiveWebServerFactory) {
	context.registerBean<WebServerFactoryCustomizerBeanPostProcessor>("webServerFactoryCustomizerBeanPostProcessor")

	val config = ReactiveWebServerFactoryAutoConfiguration()
	context.registerBean { config.reactiveWebServerFactoryCustomizer(serverProperties) }
	context.registerBean { serverFactory }

	context.registerBean<ErrorAttributes> { DefaultErrorAttributes(serverProperties.error.isIncludeException) }
	context.registerBean {
		ErrorWebFluxAutoConfiguration(serverProperties, resourceProperties,
				context.defaultListableBeanFactory.getBeanProvider(ResolvableType
						.forClassWithGenerics(List::class.java, ViewResolver::class.java)),
				context.getBean(), context)
				.errorWebExceptionHandler(context.getBean(ErrorAttributes::class.java))
	}

	context.registerBean { EnableWebFluxConfigurationWrapper(context, webFluxProperties) }

	context.registerBean(WebHttpHandlerBuilder.LOCALE_CONTEXT_RESOLVER_BEAN_NAME) {
		context.getBean<EnableWebFluxConfigurationWrapper>().localeContextResolver()
	}
	context.registerBean{
		context.getBean<EnableWebFluxConfigurationWrapper>().responseStatusExceptionHandler()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().routerFunctionMapping()
	}
	context.registerBean(WebHttpHandlerBuilder.SERVER_CODEC_CONFIGURER_BEAN_NAME) {
		context.getBean<EnableWebFluxConfigurationWrapper>().serverCodecConfigurer()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().webFluxAdapterRegistry()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().handlerFunctionAdapter()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().responseBodyResultHandler()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().responseEntityResultHandler()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().webFluxContentTypeResolver()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().requestMappingHandlerAdapter()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().serverResponseResultHandler()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().simpleHandlerAdapter()
	}
	context.registerBean {
		context.getBean<EnableWebFluxConfigurationWrapper>().viewResolutionResultHandler()
	}
	context.registerBean { WebHttpHandlerBuilder.applicationContext(context).build() }
	context.registerBean(WebHttpHandlerBuilder.WEB_HANDLER_BEAN_NAME) {
		context.getBean<EnableWebFluxConfigurationWrapper>().webHandler()
	}

	context.registerBean {
		WebFluxConfig(resourceProperties, webFluxProperties, context,
						context.getBeanProvider(ResolvableType.forClassWithGenerics(
								List::class.java, HandlerMethodArgumentResolver::class.java)),
						context.getBeanProvider(ResolvableType.forClassWithGenerics(List::class.java, CodecCustomizer::class.java)),
						context.getBeanProvider(WebFluxAutoConfiguration.ResourceHandlerRegistrationCustomizer::class.java),
						context.getBeanProvider(ResolvableType.forClassWithGenerics(List::class.java, ViewResolver::class.java)))
	}
}

internal class EnableWebFluxConfigurationWrapper(context: GenericApplicationContext, webFluxProperties: WebFluxProperties) : EnableWebFluxConfiguration(webFluxProperties, context.getBeanProvider(WebFluxRegistrations::class.java))


