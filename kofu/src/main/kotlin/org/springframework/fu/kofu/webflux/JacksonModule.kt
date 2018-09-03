package org.springframework.fu.kofu.webflux

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.kofu.AbstractModule
import org.springframework.http.codec.*
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * @author Sebastien Deleuze
 */
class JacksonModule(val json: Boolean) : WebFluxCodecModule, AbstractModule() {

	val mapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean { mapper }
	}

	override fun invoke(configurer: CodecConfigurer) {
		if (json) {
			val encoder = Jackson2JsonEncoder(mapper)
			configurer.customCodecs().decoder(Jackson2JsonDecoder(mapper))
			configurer.customCodecs().encoder(encoder)
			configurer.customCodecs().writer(ServerSentEventHttpMessageWriter(encoder))
		}
	}
}

fun WebFluxCodecsModule.jackson(json: Boolean = true) : JacksonModule {
	val jacksonModule = JacksonModule(json)
	initializers.add(jacksonModule)
	return jacksonModule
}