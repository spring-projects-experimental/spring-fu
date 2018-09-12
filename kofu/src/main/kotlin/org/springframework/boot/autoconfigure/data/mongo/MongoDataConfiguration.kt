package org.springframework.boot.autoconfigure.data.mongo

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean

internal fun registerMongoDataConfiguration(context: GenericApplicationContext, properties: MongoProperties) {
	val dataConfiguration = MongoDataConfiguration(context, properties)
	context.registerBean { dataConfiguration.mongoMappingContext(context.getBean()) }
	context.registerBean { dataConfiguration.mongoCustomConversions() }
	val reactiveDataConfiguration = MongoReactiveDataAutoConfiguration(properties)
	context.registerBean { reactiveDataConfiguration.mappingMongoConverter(context.getBean(), context.getBean()) }
	context.registerBean { reactiveDataConfiguration.reactiveMongoDatabaseFactory(context.getBean()) }
	context.registerBean { reactiveDataConfiguration.reactiveMongoTemplate(context.getBean(), context.getBean()) }
}