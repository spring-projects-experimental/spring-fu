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

package org.springframework.boot.autoconfigure.mongo


import com.mongodb.async.client.MongoClientSettings
import org.springframework.boot.AbstractModule
import org.springframework.boot.ApplicationDsl
import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.ResolvableType


/**
 * @author Sebastien Deleuze
 */
open class MongoModule(
	val connectionString: String,
	private val init: MongoModule.() -> Unit
) : AbstractModule() {

	override lateinit var context: GenericApplicationContext

	@Suppress("DEPRECATION")
	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		init()

		val properties = MongoProperties()
		properties.uri = connectionString

		context.registerBean {
			MongoReactiveAutoConfiguration(context.defaultListableBeanFactory
					.getBeanProvider(MongoClientSettings::class.java)).reactiveStreamsMongoClient(properties, context.environment, context.defaultListableBeanFactory.getBeanProvider(ResolvableType.forClassWithGenerics(List::class.java, MongoClientSettingsBuilderCustomizer::class.java)))
		}
		context.registerBean {
			MongoReactiveAutoConfiguration.NettyDriverConfiguration().nettyDriverCustomizer(context.defaultListableBeanFactory.getBeanProvider(MongoClientSettings::class.java))
		}
		MongoDataInitializer(properties).initialize(context)
		super.initialize(context)
	}

}

fun ApplicationDsl.mongodb(
	connectionString: String = "mongodb://localhost/test",
	init: MongoModule.() -> Unit = {}) {
	initializers.add(MongoModule(connectionString, init))
}