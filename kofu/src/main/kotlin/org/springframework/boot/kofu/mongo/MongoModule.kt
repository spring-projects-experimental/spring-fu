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
import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer
import org.springframework.boot.autoconfigure.mongo.MongoInitializer
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.support.GenericApplicationContext


/**
 * @author Sebastien Deleuze
 */
open class MongoModule(
	val connectionString: String,
	private val init: MongoModule.() -> Unit
) : AbstractModule() {

	override lateinit var context: GenericApplicationContext


	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		init()
		val properties = MongoProperties()
		properties.uri = connectionString
		MongoInitializer(properties).initialize(context)
		MongoDataInitializer(properties).initialize(context)
		super.initialize(context)
	}

}

fun ApplicationDsl.mongodb(
	connectionString: String = "mongodb://localhost/test",
	init: MongoModule.() -> Unit = {}) {
	initializers.add(MongoModule(connectionString, init))
}