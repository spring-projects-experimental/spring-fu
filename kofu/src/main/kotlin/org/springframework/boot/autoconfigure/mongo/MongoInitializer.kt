@file:Suppress("DEPRECATION")

package org.springframework.boot.autoconfigure.mongo

import com.mongodb.async.client.MongoClientSettings
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.ResolvableType

internal class MongoInitializer(private val properties: MongoProperties, private val embedded: Boolean) : ApplicationContextInitializer<GenericApplicationContext> {

	override fun initialize(context: GenericApplicationContext) {
		context.registerBean(BeanDefinitionCustomizer {
			if (embedded) it.setDependsOn("embeddedMongoServer")
		}) {
			MongoReactiveAutoConfiguration(context.defaultListableBeanFactory
					.getBeanProvider(MongoClientSettings::class.java)).reactiveStreamsMongoClient(properties, context.environment, context.defaultListableBeanFactory.getBeanProvider(ResolvableType.forClassWithGenerics(List::class.java, MongoClientSettingsBuilderCustomizer::class.java)))
		}
		context.registerBean {
			MongoReactiveAutoConfiguration.NettyDriverConfiguration().nettyDriverCustomizer(context.defaultListableBeanFactory.getBeanProvider(MongoClientSettings::class.java))
		}
	}
}