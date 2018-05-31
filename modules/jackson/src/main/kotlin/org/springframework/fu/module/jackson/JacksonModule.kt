package org.springframework.fu.module.jackson

import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.codec.*
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.fu.module.webflux.WebFluxModule

/**
 * @author Sebastien Deleuze
 */
class JacksonModule(val json: Boolean) : WebFluxModule.WebFluxServerCodecModule {

	override fun initialize(applicationContext: GenericApplicationContext) {
	}

	override fun invoke(configurer: ServerCodecConfigurer) {
		if (json) {
			val encoder = Jackson2JsonEncoder()
			configurer.customCodecs().decoder(Jackson2JsonDecoder())
			configurer.customCodecs().encoder(encoder)
			configurer.customCodecs().writer(ServerSentEventHttpMessageWriter(encoder))
		}
	}
}

fun WebFluxModule.WebFluxCodecsModule.jackson(json: Boolean = true) : JacksonModule {
	val jacksonModule = JacksonModule(json)
	children.add(jacksonModule)
	return jacksonModule
}