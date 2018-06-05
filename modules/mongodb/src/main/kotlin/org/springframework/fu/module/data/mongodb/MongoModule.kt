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

package org.springframework.fu.module.data.mongodb

import com.mongodb.DBRef
import com.mongodb.MongoClientURI
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.Document
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.*
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty
import org.springframework.fu.ApplicationDsl
import org.springframework.fu.ContainerModule


/**
 * @author Sebastien Deleuze
 */
open class MongoModule(private val connectionString: String, private val init: MongoModule.() -> Unit) : ContainerModule() {

	override lateinit var context: GenericApplicationContext

	override fun initialize(context : GenericApplicationContext) {
		this.context = context
		init()

		context.registerBean {
			MongoClients.create(connectionString)
		}
		context.registerBean {
			SimpleReactiveMongoDatabaseFactory(context.getBean(), MongoClientURI(connectionString).database)
		}
		context.registerBean {
			val conversions = MongoCustomConversions(emptyList<Any>())
			val mappingContext = MongoMappingContext().apply {
				setSimpleTypeHolder(conversions.simpleTypeHolder)
				afterPropertiesSet()
			}
			MappingMongoConverter(NoOpDbRefResolver(), mappingContext).apply {
				setCustomConversions(conversions)
				afterPropertiesSet()
			}
		}
		context.registerBean<ReactiveMongoOperations> {
			ReactiveMongoTemplate(context.getBean(), context.getBean<MongoConverter>())
		}
		super.initialize(context)
	}

	class NoOpDbRefResolver : DbRefResolver {
		override fun resolveDbRef(property: MongoPersistentProperty, dbref: DBRef, callback: DbRefResolverCallback, proxyHandler: DbRefProxyHandler): Any? {
			return null
		}

		override fun createDbRef(annotation: org.springframework.data.mongodb.core.mapping.DBRef, entity: MongoPersistentEntity<*>, id: Any): DBRef {
			return DBRef("", 0)
		}

		override fun fetch(dbRef: DBRef): Document? {
			return null
		}

		override fun bulkFetch(dbRefs: MutableList<DBRef>): List<Document> {
			return emptyList()
		}
	}
}

fun ApplicationDsl.mongodb(connectionString: String = "mongodb://localhost/test", init: MongoModule.() -> Unit) : MongoModule {
	val mongoModule = MongoModule(connectionString, init)
	modules.add(mongoModule)
	return mongoModule
}