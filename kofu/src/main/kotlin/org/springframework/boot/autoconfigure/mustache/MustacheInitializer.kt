package org.springframework.boot.autoconfigure.mustache

import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

internal class MustacheInitializer(private val properties: MustacheProperties) : ApplicationContextInitializer<GenericApplicationContext> {

	override fun initialize(context: GenericApplicationContext) {
		val mustacheConfiguration = MustacheAutoConfiguration(properties, context.environment, context)
		context.registerBean {
			mustacheConfiguration.mustacheTemplateLoader()
		}
		context.registerBean {
			mustacheConfiguration.mustacheCompiler(context.getBean())
		}
		val mustacheReactiveConfiguration = MustacheReactiveWebConfiguration(properties)
		context.registerBean {
			mustacheReactiveConfiguration.mustacheViewResolver(context.getBean())
		}
	}
}