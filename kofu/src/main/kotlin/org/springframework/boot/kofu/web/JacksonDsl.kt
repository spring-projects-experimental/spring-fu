package org.springframework.boot.kofu.web

import org.springframework.boot.autoconfigure.jackson.JacksonInitializer
import org.springframework.boot.autoconfigure.jackson.JacksonJsonReactiveWebInitializer
import org.springframework.boot.autoconfigure.jackson.JacksonProperties

fun WebFluxCodecsDsl.jackson(json: Boolean = true) {
	val properties = JacksonProperties()
	initializers.add(JacksonInitializer(properties))
	if (json) {
		initializers.add(JacksonJsonReactiveWebInitializer())
	}
}
