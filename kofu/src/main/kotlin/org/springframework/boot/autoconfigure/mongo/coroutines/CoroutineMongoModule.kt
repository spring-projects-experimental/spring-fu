package org.springframework.boot.autoconfigure.mongo.coroutines

import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.boot.AbstractModule
import org.springframework.boot.autoconfigure.mongo.MongoModule
import org.springframework.fu.coroutines.mongodb.data.mongodb.core.CoroutineMongoTemplate

class CoroutineMongoModule : AbstractModule() {
	override fun initialize(context: GenericApplicationContext) {
		context.registerBean { CoroutineMongoTemplate(context.getBean()) }
		context.registerBean {
			BeanFactoryPostProcessor { beanFactory ->
				val registry = (beanFactory as BeanDefinitionRegistry) //TODO: fix this hack

				registry.beanDefinitionNames
					.filter { it.startsWith("coroutine_") }
					.map { it.substringAfter("coroutine_") }
					.forEach {
						registry.removeBeanDefinition(it)
						registry.registerAlias("coroutine_$it", it)
					}
			}

		}
	}
}

fun MongoModule.coroutines(): CoroutineMongoModule {
	val coroutineModule = CoroutineMongoModule()
	initializers.add(coroutineModule)
	return coroutineModule
}