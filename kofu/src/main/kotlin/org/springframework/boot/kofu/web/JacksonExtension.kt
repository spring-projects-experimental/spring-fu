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

import org.springframework.boot.autoconfigure.jackson.JacksonInitializer
import org.springframework.boot.autoconfigure.jackson.JacksonJsonCodecInitializer
import org.springframework.boot.autoconfigure.jackson.JacksonProperties

/**
 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
 * JSON codec on WebFlux server and client.
 *
 * Require `org.springframework.boot:spring-boot-starter-json` dependency
 * (included by default in `spring-boot-starter-webflux`).
 *
 * @sample org.springframework.boot.kofu.samples.jackson
 */
fun WebFluxCodecsDsl.jackson(json: Boolean = true) {
	val properties = JacksonProperties()
	initializers.add(JacksonInitializer(properties))
	if (json) {
		initializers.add(JacksonJsonCodecInitializer())
	}
}
