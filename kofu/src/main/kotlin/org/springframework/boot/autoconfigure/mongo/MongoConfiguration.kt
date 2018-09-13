@file:Suppress("DEPRECATION")

package org.springframework.boot.autoconfigure.mongo

import com.mongodb.MongoClientSettings
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.ResolvableType

internal fun registerMongoConfiguration(context:GenericApplicationContext, properties: MongoProperties, embedded: Boolean) {
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