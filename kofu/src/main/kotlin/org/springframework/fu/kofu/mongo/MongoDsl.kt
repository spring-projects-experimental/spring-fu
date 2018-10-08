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

package org.springframework.fu.kofu.mongo

import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataInitializer
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.autoconfigure.mongo.MongoReactiveInitializer
import org.springframework.boot.autoconfigure.mongo.coroutines.CoroutinesMongoInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ApplicationDsl

/**
 * Kofu DSL for MongoDB configuration.
 * @author Sebastien Deleuze
 */
open class MongoDsl(
	private val init: MongoDsl.() -> Unit
) : AbstractDsl() {

	override lateinit var context: GenericApplicationContext

	internal val properties = MongoProperties()

	internal var embedded = false

	override fun register(context: GenericApplicationContext) {
		init()
		MongoDataInitializer(properties).initialize(context)
		MongoReactiveDataInitializer(properties).initialize(context)
		MongoReactiveInitializer(properties, embedded).initialize(context)
	}

	var coroutines: Boolean = false
		set(value) {
			if (value) initializers.add(CoroutinesMongoInitializer())
		}

	var uri: String? = "mongodb://localhost/test"
		set(value) {
			properties.uri = value
		}
}

/**
 * Enable and [configure][MongoDsl] Reactive MongoDB support by registering a [org.springframework.data.mongodb.core.ReactiveMongoTemplate].
 *
 * Require `org.springframework.boot:spring-boot-starter-data-mongodb-reactive` dependency.
 *
 * @sample org.springframework.boot.kofu.samples.mongo
 */
fun ApplicationDsl.mongodb(dsl: MongoDsl.() -> Unit = {}) {
	initializers.add(MongoDsl(dsl))
}