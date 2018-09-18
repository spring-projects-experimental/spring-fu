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

package org.springframework.boot.kofu.web

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
import org.springframework.boot.kofu.AbstractDsl
import org.springframework.context.support.GenericApplicationContext
import java.util.*
import kotlin.reflect.KClass

class JacksonDsl(private val properties: JacksonProperties, private val init: JacksonDsl.() -> Unit): AbstractDsl() {

	override fun register(context: GenericApplicationContext) {
		init()
		initializers.add(JacksonInitializer(properties))
		initializers.add(JacksonJsonCodecInitializer())
	}

	/**
	 * Date format string or a fully-qualified date format class name. For instance, `yyyy-MM-dd HH:mm:ss`
	 */
	fun dateFormat(dateFormat: String) {
		properties.dateFormat = dateFormat
	}

	/**
	 * [PropertyNamingStrategy] constant or subclass
	 */
	fun propertyNamingStrategy(propertyNamingStrategy: KClass<PropertyNamingStrategy>) {
		properties.propertyNamingStrategy = propertyNamingStrategy.qualifiedName
	}

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

	/**
	 * Set the default property inclusion
	 */
	fun defaultPropertyInclusion(defaultPropertyInclusion: JsonInclude.Include) {
		properties.defaultPropertyInclusion = defaultPropertyInclusion
	}

	/**
	 * Set the timezone
	 */

	fun timezone(timeZone: TimeZone) {
		properties.timeZone = timeZone
	}

	/**
	 * Set the locale
	 */
	fun locale(locale: Locale) {
		properties.locale = locale
	}


}

/**
 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
 * JSON codec on WebFlux server and client.
 *
 * Require `org.springframework.boot:spring-boot-starter-json` dependency
 * (included by default in `spring-boot-starter-webflux`).
 * @param dateFormat
 * @param propertyNamingStrategy [PropertyNamingStrategy] constant or subclass

 * @sample org.springframework.boot.kofu.samples.jacksonDsl
 */
fun WebFluxCodecsDsl.jackson(dsl: JacksonDsl.() -> Unit = {}) {
	val properties = JacksonProperties()
	initializers.add(JacksonDsl(properties, dsl))
}
