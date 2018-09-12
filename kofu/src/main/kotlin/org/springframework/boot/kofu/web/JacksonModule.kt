package org.springframework.boot.kofu.web

import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.autoconfigure.jackson.registerJacksonConfiguration
import org.springframework.boot.kofu.AbstractModule
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.codec.ServerSentEventHttpMessageWriter
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder


/**
 * @author Sebastien Deleuze
 */
internal class JacksonModule(private val json: Boolean, private val codecModule: WebFluxCodecsModule) : AbstractModule() {

	private val properties = JacksonProperties()

	override fun registerBeans(context: GenericApplicationContext) {
		registerJacksonConfiguration(context, properties)
		if (json) {
			with(codecModule) {
				// TODO Use ObjectMapper bean
				val encoder = Jackson2JsonEncoder()
				decoders.add(Jackson2JsonDecoder())
				encoders.add(encoder)
				writers.add(ServerSentEventHttpMessageWriter(encoder))
			}
		}
	}
}

fun WebFluxCodecsModule.jackson(json: Boolean = true) {
	initializers.add(JacksonModule(json, this))
}