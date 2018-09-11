package org.springframework.boot.autoconfigure.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeansOfType
import org.springframework.boot.AbstractModule
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.*
import org.springframework.boot.autoconfigure.web.reactive.WebFluxCodecModule
import org.springframework.boot.autoconfigure.web.reactive.WebFluxCodecsModule
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.http.codec.CodecConfigurer
import org.springframework.http.codec.ServerSentEventHttpMessageWriter
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * @author Sebastien Deleuze
 */
internal class JacksonModule(private val json: Boolean) : WebFluxCodecModule, AbstractModule() {

	override fun initialize(context: GenericApplicationContext) {
		val properties = JacksonProperties()

		context.registerBean<Jackson2ObjectMapperBuilderCustomizer> {
			@Suppress("INACCESSIBLE_TYPE")
			Jackson2ObjectMapperBuilderCustomizerConfiguration().standardJacksonObjectMapperBuilderCustomizer(context, properties)
		}
		context.registerBean { JacksonObjectMapperBuilderConfiguration(context).jacksonObjectMapperBuilder(context.getBeansOfType<Jackson2ObjectMapperBuilderCustomizer>().map { it.value }) }
		context.registerBean { JacksonObjectMapperConfiguration().jacksonObjectMapper(context.getBean())}
	}

	override fun invoke(context: ApplicationContext, configurer: CodecConfigurer) {
		if (json) {
			val mapper: ObjectMapper = context.getBean()
			val encoder = Jackson2JsonEncoder(mapper)
			configurer.customCodecs().decoder(Jackson2JsonDecoder(mapper))
			configurer.customCodecs().encoder(encoder)
			configurer.customCodecs().writer(ServerSentEventHttpMessageWriter(encoder))
		}
	}
}

fun WebFluxCodecsModule.jackson(json: Boolean = true) {
	initializers.add(JacksonModule(json))
}