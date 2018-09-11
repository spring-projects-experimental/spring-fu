package org.springframework.boot.kofu.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.jackson.JacksonInitializer
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.kofu.AbstractModule
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.codec.CodecConfigurer
import org.springframework.http.codec.ServerSentEventHttpMessageWriter
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder

/**
 * @author Sebastien Deleuze
 */
internal class JacksonModule(private val json: Boolean) : WebFluxCodecModule, AbstractModule() {

	override fun initialize(context: GenericApplicationContext) {
		val properties = JacksonProperties()
		JacksonInitializer(properties).initialize(context)
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