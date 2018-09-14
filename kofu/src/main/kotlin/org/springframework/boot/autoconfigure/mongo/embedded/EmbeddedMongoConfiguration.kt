package org.springframework.boot.autoconfigure.mongo.embedded

import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

internal fun registerEmbeddedMongoconfiguration(context: GenericApplicationContext, mongoProperties: MongoProperties, embeddedMongoProperties: EmbeddedMongoProperties) {
	val config = EmbeddedMongoAutoConfiguration.RuntimeConfigConfiguration().embeddedMongoRuntimeConfig()
	val configuration = EmbeddedMongoAutoConfiguration(mongoProperties, embeddedMongoProperties, context, config)
	context.registerBean { configuration.embeddedMongoConfiguration() }
	val customizer = BeanDefinitionCustomizer {
		it.initMethodName = "start"
		it.destroyMethodName = "stop"
	}
	context.registerBean("embeddedMongoServer", customizer) { configuration.embeddedMongoServer(context.getBean())}
	context.registerBean { config }
}