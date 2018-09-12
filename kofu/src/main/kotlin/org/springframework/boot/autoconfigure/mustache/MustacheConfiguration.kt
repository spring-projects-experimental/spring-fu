package org.springframework.boot.autoconfigure.mustache

import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

internal fun registerMustacheConfiguration(context: GenericApplicationContext, properties: MustacheProperties){
	val mustacheConfiguration = MustacheAutoConfiguration(properties, context.environment, context)
	context.registerBean {
		mustacheConfiguration.mustacheTemplateLoader()
	}
	context.registerBean {
		mustacheConfiguration.mustacheCompiler(context.getBean())
	}
	context.registerBean {
		MustacheReactiveWebConfiguration(properties).mustacheViewResolver(context.getBean())
	}
}