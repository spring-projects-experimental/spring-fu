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


import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataInitializer
import org.springframework.boot.kofu.AbstractDsl
import org.springframework.boot.kofu.ApplicationDsl
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.autoconfigure.mongo.MongoReactiveInitializer
import org.springframework.context.support.GenericApplicationContext


/**
 * @author Sebastien Deleuze
 */
open class MongoDsl(
	internal val properties: MongoProperties,
	private val init: MongoDsl.() -> Unit
) : AbstractDsl() {

	override lateinit var context: GenericApplicationContext

	internal var embedded = false

	override fun register(context: GenericApplicationContext) {
		init()
		MongoDataInitializer(properties).initialize(context)
		MongoReactiveDataInitializer(properties).initialize(context)
		MongoReactiveInitializer(properties, embedded).initialize(context)
	}

}

fun ApplicationDsl.mongodb(
	connectionString: String = "mongodb://localhost/test",
	init: MongoDsl.() -> Unit = {}) {
	val properties = MongoProperties()
	properties.uri = connectionString
	initializers.add(MongoDsl(properties, init))
}