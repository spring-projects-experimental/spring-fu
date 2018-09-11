package org.springframework.boot.autoconfigure.data.mongo

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

internal class MongoDataInitializer(private val properties: MongoProperties) : ApplicationContextInitializer<GenericApplicationContext> {

	override fun initialize(context: GenericApplicationContext) {
		val dataConfiguration = MongoDataConfiguration(context, properties)
		context.registerBean { dataConfiguration.mongoMappingContext(context.getBean()) }
		context.registerBean { dataConfiguration.mongoCustomConversions() }

		val reactiveDataConfiguration = MongoReactiveDataAutoConfiguration(properties)
		context.registerBean { reactiveDataConfiguration.mappingMongoConverter(context.getBean(), context.getBean()) }
		context.registerBean { reactiveDataConfiguration.reactiveMongoDatabaseFactory(context.getBean()) }
		context.registerBean { reactiveDataConfiguration.reactiveMongoTemplate(context.getBean(), context.getBean()) }
	}
}