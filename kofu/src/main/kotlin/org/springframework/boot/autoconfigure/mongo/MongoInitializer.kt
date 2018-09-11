@file:Suppress("DEPRECATION")

package org.springframework.boot.autoconfigure.mongo

import com.mongodb.async.client.MongoClientSettings
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.ResolvableType

internal class MongoInitializer(private val properties: MongoProperties) : ApplicationContextInitializer<GenericApplicationContext> {

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean {
			MongoReactiveAutoConfiguration(context.defaultListableBeanFactory
					.getBeanProvider(MongoClientSettings::class.java)).reactiveStreamsMongoClient(properties, context.environment, context.defaultListableBeanFactory.getBeanProvider(ResolvableType.forClassWithGenerics(List::class.java, MongoClientSettingsBuilderCustomizer::class.java)))
		}
		context.registerBean {
			MongoReactiveAutoConfiguration.NettyDriverConfiguration().nettyDriverCustomizer(context.defaultListableBeanFactory.getBeanProvider(MongoClientSettings::class.java))
		}
	}
}