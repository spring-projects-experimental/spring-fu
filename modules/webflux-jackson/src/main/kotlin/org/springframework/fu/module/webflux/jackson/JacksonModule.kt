package org.springframework.fu.module.webflux.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.http.codec.*
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * @author Sebastien Deleuze
 */
class JacksonModule(val json: Boolean) : WebFluxModule.WebFluxServerCodecModule, AbstractModule() {

	val mapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean { mapper }
	}

	override fun invoke(configurer: ServerCodecConfigurer) {
		if (json) {
			val encoder = Jackson2JsonEncoder(mapper)
			configurer.customCodecs().decoder(Jackson2JsonDecoder(mapper))
			configurer.customCodecs().encoder(encoder)
			configurer.customCodecs().writer(ServerSentEventHttpMessageWriter(encoder))
		}
	}
}

fun WebFluxModule.WebFluxCodecsModule.jackson(json: Boolean = true) : JacksonModule {
	val jacksonModule = JacksonModule(json)
	initializers.add(jacksonModule)
	return jacksonModule
}