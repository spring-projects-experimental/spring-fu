package org.springframework.fu.module.data.mongodb.coroutines

import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.data.mongodb.coroutines.data.mongodb.core.CoroutineMongoTemplate
import org.springframework.fu.module.data.mongodb.MongoModule

class CoroutinesMongoModule : AbstractModule() {
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

fun MongoModule.coroutines() : CoroutinesMongoModule {
	val coroutineModule = CoroutinesMongoModule()
	initializers.add(coroutineModule)
	return coroutineModule
}