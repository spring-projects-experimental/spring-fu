/*
 * Copyright 2012-2020 the original author or authors.
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

import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataInitializer
import org.springframework.boot.autoconfigure.mongo.MongoReactiveInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for MongoDB configuration.
 *
 * Enable and configure Reactive MongoDB support by registering a [org.springframework.data.mongodb.core.ReactiveMongoTemplate].
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-data-mongodb-reactive`.
 *
 * @sample org.springframework.fu.kofu.samples.mongo
 * @author Sebastien Deleuze
 * @author Eddú Meléndez
 */
open class ReactiveMongoDsl(
	private val init: ReactiveMongoDsl.() -> Unit
) : AbstractMongoDsl({}) {

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		MongoReactiveDataInitializer(properties).initialize(context)
		MongoReactiveInitializer(properties, embedded).initialize(context)
	}

}

/**
 * Configure MongoDB Reactive support.
 * @see ReactiveMongoDsl
 */
fun ConfigurationDsl.reactiveMongodb(dsl: ReactiveMongoDsl.() -> Unit = {}) {
	ReactiveMongoDsl(dsl).initialize(context)
}
