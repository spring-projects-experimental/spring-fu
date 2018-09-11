/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.kofu.mongo

import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoInitializer
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties
import org.springframework.boot.kofu.AbstractModule
import org.springframework.context.support.GenericApplicationContext

class EmbeddedMongoModule(private val mongoProperties: MongoProperties) : AbstractModule() {

	private val embeddedMongoProperties = EmbeddedMongoProperties()

	override fun initialize(context: GenericApplicationContext) {
		EmbeddedMongoInitializer(mongoProperties, embeddedMongoProperties).initialize(context)
	}

	fun version(version: String) {
		embeddedMongoProperties.version = version
	}
}

fun MongoModule.embedded() {
	val embeddedMongoModule = EmbeddedMongoModule(properties)
	embedded = true
	initializers.add(embeddedMongoModule)
}
