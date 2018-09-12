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


import org.springframework.boot.kofu.AbstractModule
import org.springframework.boot.kofu.ApplicationDsl
import org.springframework.boot.autoconfigure.data.mongo.registerMongoDataConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.autoconfigure.mongo.registerMongoConfiguration
import org.springframework.context.support.GenericApplicationContext


/**
 * @author Sebastien Deleuze
 */
open class MongoModule(
	internal val properties: MongoProperties,
	private val init: MongoModule.() -> Unit
) : AbstractModule() {

	override lateinit var context: GenericApplicationContext

	internal var embedded = false


	override fun registerBeans(context: GenericApplicationContext) {
		init()
		registerMongoConfiguration(context, properties, embedded)
		registerMongoDataConfiguration(context, properties)
	}

}

fun ApplicationDsl.mongodb(
	connectionString: String = "mongodb://localhost/test",
	init: MongoModule.() -> Unit = {}) {
	val properties = MongoProperties()
	properties.uri = connectionString
	initializers.add(MongoModule(properties, init))
}