@file:Suppress("DEPRECATION")

package org.springframework.boot.autoconfigure.mongo

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.beans.factory.config.DependencyDescriptor
import org.springframework.beans.factory.getBeanProvider
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.MethodParameter
import org.springframework.core.env.Environment

internal fun registerMongoConfiguration(context:GenericApplicationContext, properties: MongoProperties, embedded: Boolean) {
	context.registerBean(BeanDefinitionCustomizer {
		if (embedded) it.setDependsOn("embeddedMongoServer")
	}) {
		val customizers = context.defaultListableBeanFactory.resolveDependency(DependencyDescriptor(MethodParameter.forParameter(MongoReactiveAutoConfiguration::class.java.getDeclaredMethod("reactiveStreamsMongoClient", MongoProperties::class.java, Environment::class.java, ObjectProvider::class.java).parameters[2]), true), null) as ObjectProvider<List<MongoClientSettingsBuilderCustomizer>>
		MongoReactiveAutoConfiguration(context.getBeanProvider()).reactiveStreamsMongoClient(properties, context.environment, customizers)
	}
	context.registerBean {
		MongoReactiveAutoConfiguration.NettyDriverConfiguration().nettyDriverCustomizer(context.defaultListableBeanFactory.getBeanProvider())
	}
}