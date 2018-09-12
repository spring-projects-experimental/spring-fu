package org.springframework.boot.autoconfigure.jackson;

import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

internal fun registerJacksonConfiguration(context: GenericApplicationContext, properties: JacksonProperties) {
	context.registerBean<Jackson2ObjectMapperBuilderCustomizer> {
		@Suppress("INACCESSIBLE_TYPE")
		JacksonAutoConfiguration.Jackson2ObjectMapperBuilderCustomizerConfiguration().standardJacksonObjectMapperBuilderCustomizer(context, properties)
	}
	context.registerBean { JacksonAutoConfiguration.JacksonObjectMapperBuilderConfiguration(context).jacksonObjectMapperBuilder(context.getBeansOfType<Jackson2ObjectMapperBuilderCustomizer>().map { it.value }) }
	context.registerBean { JacksonAutoConfiguration.JacksonObjectMapperConfiguration().jacksonObjectMapper(context.getBean())}
}
