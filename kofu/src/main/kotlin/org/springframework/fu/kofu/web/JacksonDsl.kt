/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.kofu.web

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.boot.autoconfigure.jackson.JacksonInitializer
import org.springframework.boot.autoconfigure.jackson.JacksonJsonCodecInitializer
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.fu.kofu.AbstractDsl
import java.util.*
import kotlin.reflect.KClass

class JacksonDsl(private val isClientCodec: Boolean, private val init: JacksonDsl.() -> Unit): AbstractDsl() {

	private val properties = JacksonProperties()

	override fun register() {
		init()
		if (dateFormat != null) {
			properties.dateFormat = dateFormat
		}
		if (propertyNamingStrategy != null) {
			properties.propertyNamingStrategy = propertyNamingStrategy!!.qualifiedName
		}
		if (defaultPropertyInclusion != null) {
			properties.defaultPropertyInclusion = defaultPropertyInclusion
		}
		if (timeZone != null) {
			properties.timeZone = timeZone
		}
		if (locale != null) {
			properties.locale = locale
		}
		if (indentOutput != null) {
			properties.serialization[SerializationFeature.INDENT_OUTPUT] = indentOutput
		}
		JacksonInitializer(properties).initialize(context)
		JacksonJsonCodecInitializer(isClientCodec).initialize(context)
	}

	/**
	 * Date format string or a fully-qualified date format class name. For instance, `yyyy-MM-dd HH:mm:ss`
	 */
	var dateFormat: String? = null

	/**
	 * [PropertyNamingStrategy] constant or subclass
	 */
	var propertyNamingStrategy: KClass<PropertyNamingStrategy>? = null

	/**
	 * Set the default property inclusion
	 */
	var defaultPropertyInclusion: JsonInclude.Include? = null

	/**
	 * Set the timezone
	 */
	var timeZone: TimeZone? = null

	/**
	 * Set the locale
	 */
	var locale: Locale? = null

	/**
	 * Shortcut for {@link SerializationFeature#INDENT_OUTPUT} feature.
	 */
	var indentOutput: Boolean? = null


	/**
	 * Set the visibility for the specified [PropertyAccessor]
	 */
	fun visibility(propertyAccessor: PropertyAccessor, visibility: JsonAutoDetect.Visibility) {
		properties.visibility[propertyAccessor] = visibility
	}

	/**
	 * Enable serialization feature
	 */
	fun enableSerializationFeature(feature: SerializationFeature) {
		properties.serialization[feature] = true
	}

	/**
	 * Disable serialization feature
	 */
	fun disableSerializationFeature(feature: SerializationFeature) {
		properties.serialization[feature] = false
	}

	/**
	 * Enable deserialization feature
	 */
	fun enableDeserializationFeature(feature: DeserializationFeature) {
		properties.deserialization[feature] = true
	}

	/**
	 * Disable deserialization feature
	 */
	fun disableDeserializationFeature(feature: DeserializationFeature) {
		properties.deserialization[feature] = false
	}

	/**
	 * Enable mapper feature
	 */
	fun enableMapperFeature(feature: MapperFeature) {
		properties.mapper[feature] = true
	}

	/**
	 * Disable mapper feature
	 */
	fun disableMapperFeature(feature: MapperFeature) {
		properties.mapper[feature] = false
	}

	/**
	 * Enable generator feature
	 */
	fun enableGeneratorFeature(feature: JsonGenerator.Feature) {
		properties.generator[feature] = true
	}

	/**
	 * Disable generator feature
	 */
	fun disableGeneratorFeature(feature: JsonGenerator.Feature) {
		properties.generator[feature] = false
	}

}

/**
 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
 * JSON codec on WebFlux client via a [dedicated DSL][JacksonDsl].
 *
 * Require `org.springframework.boot:spring-boot-starter-json` dependency
 * (included by default in `spring-boot-starter-webflux`).

 * @sample org.springframework.fu.kofu.samples.jacksonDsl
 */
fun WebFluxClientDsl.WebFluxClientCodecDsl.jackson(dsl: JacksonDsl.() -> Unit = {}) {
	JacksonDsl(true, dsl).initialize(context)
}

/**
 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
 * JSON codec on WebFlux client via a [dedicated DSL][JacksonDsl].
 *
 * Require `org.springframework.boot:spring-boot-starter-json` dependency
 * (included by default in `spring-boot-starter-webflux`).

 * @sample org.springframework.fu.kofu.samples.jacksonDsl
 */
fun WebFluxServerDsl.WebFluxServerCodecDsl.jackson(dsl: JacksonDsl.() -> Unit = {}) {
	JacksonDsl(false, dsl).initialize(context)
}
