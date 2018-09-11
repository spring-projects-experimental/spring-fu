package org.springframework.boot.autoconfigure.jackson;

import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

internal class JacksonInitializer(private val properties: JacksonProperties) : ApplicationContextInitializer<GenericApplicationContext> {

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean<Jackson2ObjectMapperBuilderCustomizer> {
			@Suppress("INACCESSIBLE_TYPE")
			JacksonAutoConfiguration.Jackson2ObjectMapperBuilderCustomizerConfiguration().standardJacksonObjectMapperBuilderCustomizer(context, properties)
		}
		context.registerBean { JacksonAutoConfiguration.JacksonObjectMapperBuilderConfiguration(context).jacksonObjectMapperBuilder(context.getBeansOfType<Jackson2ObjectMapperBuilderCustomizer>().map { it.value }) }
		context.registerBean { JacksonAutoConfiguration.JacksonObjectMapperConfiguration().jacksonObjectMapper(context.getBean())}
	}
}
