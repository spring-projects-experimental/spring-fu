package org.springframework.boot.autoconfigure.mustache

import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.web.reactive.result.view.ViewResolver

internal fun registerMustacheConfiguration(context: GenericApplicationContext, properties: MustacheProperties){
	val mustacheConfiguration = MustacheAutoConfiguration(properties, context.environment, context)
	context.registerBean {
		mustacheConfiguration.mustacheTemplateLoader()
	}
	context.registerBean {
		mustacheConfiguration.mustacheCompiler(context.getBean())
	}
	context.registerBean<ViewResolver> {
		MustacheReactiveWebConfiguration(properties).mustacheViewResolver(context.getBean())
	}
}