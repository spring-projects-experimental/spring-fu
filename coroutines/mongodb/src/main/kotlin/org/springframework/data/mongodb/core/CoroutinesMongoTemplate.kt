/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.data.mongodb.core

import com.mongodb.ReadPreference
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.reactive.*
import org.bson.Document
import org.springframework.data.geo.GeoResult
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.index.CoroutinesIndexOperations
import org.springframework.data.mongodb.core.index.DefaultCoroutinesIndexOperations
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

open class CoroutinesMongoTemplate(
	override val reactiveMongoOperations: ReactiveMongoOperations
) : CoroutinesMongoOperations {
	override fun indexOps(collectionName: String): CoroutinesIndexOperations =
			DefaultCoroutinesIndexOperations(reactiveMongoOperations.indexOps(collectionName))

	override fun indexOps(entityClass: Class<*>): CoroutinesIndexOperations =
			DefaultCoroutinesIndexOperations(reactiveMongoOperations.indexOps(entityClass))

	override suspend fun executeCommand(jsonCommand: String): Document? =
		reactiveMongoOperations.executeCommand(jsonCommand).awaitFirstOrDefault(null)

	override suspend fun executeCommand(command: Document): Document? =
		reactiveMongoOperations.executeCommand(command).awaitFirstOrDefault(null)

	override suspend fun executeCommand(command: Document, readPreference: ReadPreference): Document? =
		reactiveMongoOperations.executeCommand(command, readPreference).awaitFirstOrDefault(null)

	override suspend fun <T> execute(action: CoroutineDatabaseCallback<T>): List<T> =
		reactiveMongoOperations.execute(action.reactiveDatabaseCallback).collectList().awaitSingle()

	override suspend fun <T> execute(entityClass: Class<*>, action: CoroutinesCollectionCallback<T>): List<T> =
		reactiveMongoOperations.execute(entityClass, action.reactiveCollectionCallback).collectList().awaitSingle()

	override suspend fun <T> execute(collectionName: String, action: CoroutinesCollectionCallback<T>): List<T> =
		reactiveMongoOperations.execute(collectionName, action.reactiveCollectionCallback).collectList().awaitSingle()

	override suspend fun <T> createCollection(entityClass: Class<T>): CoroutinesMongoCollection<Document> =
		reactiveMongoOperations.createCollection(entityClass).awaitFirstOrDefault(null).asCoroutineMongoCollection()

	override suspend fun <T> createCollection(
		entityClass: Class<T>,
		collectionOptions: CollectionOptions
	): CoroutinesMongoCollection<Document> =
		reactiveMongoOperations.createCollection(
			entityClass,
			collectionOptions
		).awaitFirstOrDefault(null).asCoroutineMongoCollection()

	override suspend fun createCollection(collectionName: String): CoroutinesMongoCollection<Document> =
		reactiveMongoOperations.createCollection(collectionName).awaitFirstOrDefault(null).asCoroutineMongoCollection()

	override suspend fun createCollection(
		collectionName: String,
		collectionOptions: CollectionOptions
	): CoroutinesMongoCollection<Document> =
		reactiveMongoOperations.createCollection(
			collectionName,
			collectionOptions
		).awaitFirstOrDefault(null).asCoroutineMongoCollection()

	override suspend fun getCollectionNames(): List<String> =
		reactiveMongoOperations.collectionNames.collectList().awaitSingle()

	override fun getCollection(collectionName: String): CoroutinesMongoCollection<Document> =
		reactiveMongoOperations.getCollection(collectionName).asCoroutineMongoCollection()

	override suspend fun <T> collectionExists(entityClass: Class<T>): Boolean =
		reactiveMongoOperations.collectionExists(entityClass).awaitFirst()

	override suspend fun collectionExists(collectionName: String): Boolean =
		reactiveMongoOperations.collectionExists(collectionName).awaitFirst()

	override suspend fun <T> dropCollection(entityClass: Class<T>) {
		reactiveMongoOperations.dropCollection(entityClass).awaitLast()
	}

	override suspend fun dropCollection(collectionName: String) {
		reactiveMongoOperations.dropCollection(collectionName).awaitLast()
	}

	override suspend fun <T> findAll(entityClass: Class<T>): List<T> =
		reactiveMongoOperations.findAll(entityClass).collectList().awaitSingle()

	override suspend fun <T> findAll(entityClass: Class<T>, collectionName: String): List<T> =
		reactiveMongoOperations.findAll(entityClass, collectionName).collectList().awaitSingle()

	override suspend fun <T> findOne(query: Query, entityClass: Class<T>): T? =
		reactiveMongoOperations.findOne(query, entityClass).awaitFirstOrDefault(null)

	override suspend fun <T> findOne(query: Query, entityClass: Class<T>, collectionName: String): T? =
		reactiveMongoOperations.findOne(query, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun exists(query: Query, collectionName: String): Boolean =
		reactiveMongoOperations.exists(query, collectionName).awaitFirst()

	override suspend fun exists(query: Query, entityClass: Class<*>): Boolean =
		reactiveMongoOperations.exists(query, entityClass).awaitFirst()

	override suspend fun exists(query: Query, entityClass: Class<*>, collectionName: String): Boolean =
		reactiveMongoOperations.exists(query, entityClass, collectionName).awaitFirst()

	override suspend fun <T> find(query: Query, entityClass: Class<T>): List<T> =
		reactiveMongoOperations.find(query, entityClass).collectList().awaitSingle()

	override suspend fun <T> find(query: Query, entityClass: Class<T>, collectionName: String): List<T> =
		reactiveMongoOperations.find(query, entityClass, collectionName).collectList().awaitSingle()

	override suspend fun <T> findById(id: Any, entityClass: Class<T>): T? =
		reactiveMongoOperations.findById(id, entityClass).awaitFirstOrDefault(null)

	override suspend fun <T> findById(id: Any, entityClass: Class<T>, collectionName: String): T? =
		reactiveMongoOperations.findById(id, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun <O> aggregate(
		aggregation: TypedAggregation<*>,
		collectionName: String,
		outputType: Class<O>
	): List<O> =
		reactiveMongoOperations.aggregate(aggregation, collectionName, outputType).collectList().awaitSingle()

	override suspend fun <O> aggregate(aggregation: TypedAggregation<*>, outputType: Class<O>): List<O> =
		reactiveMongoOperations.aggregate(aggregation, outputType).collectList().awaitSingle()

	override suspend fun <O> aggregate(aggregation: Aggregation, inputType: Class<*>, outputType: Class<O>): List<O> =
		reactiveMongoOperations.aggregate(aggregation, inputType, outputType).collectList().awaitSingle()

	override suspend fun <O> aggregate(
		aggregation: Aggregation,
		collectionName: String,
		outputType: Class<O>
	): List<O> =
		reactiveMongoOperations.aggregate(aggregation, collectionName, outputType).collectList().awaitSingle()

	override suspend fun <T> geoNear(near: NearQuery, entityClass: Class<T>): List<GeoResult<T>> =
		reactiveMongoOperations.geoNear(near, entityClass).collectList().awaitSingle()

	override suspend fun <T> geoNear(
		near: NearQuery,
		entityClass: Class<T>,
		collectionName: String
	): List<GeoResult<T>> =
		reactiveMongoOperations.geoNear(near, entityClass, collectionName).collectList().awaitSingle()

	override suspend fun <T> findAndModify(query: Query, update: Update, entityClass: Class<T>): T? =
		reactiveMongoOperations.findAndModify(query, update, entityClass).awaitFirstOrDefault(null)

	override suspend fun <T> findAndModify(
		query: Query,
		update: Update,
		entityClass: Class<T>,
		collectionName: String
	): T? =
		reactiveMongoOperations.findAndModify(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun <T> findAndModify(
		query: Query,
		update: Update,
		options: FindAndModifyOptions,
		entityClass: Class<T>
	): T? =
		reactiveMongoOperations.findAndModify(query, update, options, entityClass).awaitFirstOrDefault(null)

	override suspend fun <T> findAndModify(
		query: Query,
		update: Update,
		options: FindAndModifyOptions,
		entityClass: Class<T>,
		collectionName: String
	): T? =
		reactiveMongoOperations.findAndModify(query, update, options, entityClass, collectionName).awaitFirstOrDefault(
			null
		)

	override suspend fun <T> findAndRemove(query: Query, entityClass: Class<T>): T? =
		reactiveMongoOperations.findAndRemove(query, entityClass).awaitFirstOrDefault(null)

	override suspend fun <T> findAndRemove(query: Query, entityClass: Class<T>, collectionName: String): T? =
		reactiveMongoOperations.findAndRemove(query, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun count(query: Query, entityClass: Class<*>): Long =
		reactiveMongoOperations.count(query, entityClass).awaitFirst()

	override suspend fun count(query: Query, collectionName: String): Long =
		reactiveMongoOperations.count(query, collectionName).awaitFirst()

	override suspend fun count(query: Query, entityClass: Class<*>, collectionName: String): Long =
		reactiveMongoOperations.count(query, entityClass, collectionName).awaitFirst()

	override suspend fun <T> insert(objectToSave: T): T? =
		reactiveMongoOperations.insert(objectToSave).awaitFirstOrDefault(null)

	override suspend fun <T> insert(objectToSave: T, collectionName: String): T? =
		reactiveMongoOperations.insert(objectToSave, collectionName).awaitFirstOrDefault(null)

	override suspend fun <T> insert(batchToSave: Collection<T>, entityClass: Class<*>): List<T> =
		reactiveMongoOperations.insert(batchToSave, entityClass).collectList().awaitSingle()

	override suspend fun <T> insert(batchToSave: Collection<T>, collectionName: String): List<T> =
		reactiveMongoOperations.insert(batchToSave, collectionName).collectList().awaitSingle()

	override suspend fun <T> insertAll(objectsToSave: Collection<T>): List<T> =
		reactiveMongoOperations.insertAll(objectsToSave).collectList().awaitSingle()

	override suspend fun <T> insert(objectToSave: suspend () -> T?): T? =
		objectToSave()?.let {
			reactiveMongoOperations.insert(it).awaitFirstOrDefault(null)
		}

	override suspend fun <T> insertAll(batchToSave: suspend () -> Collection<T>, entityClass: Class<*>): List<T> =
		insert(batchToSave(), entityClass)

	override suspend fun <T> insertAll(batchToSave: suspend () -> Collection<T>, collectionName: String): List<T> =
		insert(batchToSave(), collectionName)

	override suspend fun <T> insertAll(objectsToSave: suspend () -> Collection<T>): List<T> =
		insertAll(objectsToSave())

	override suspend fun <T> save(objectToSave: T): T? =
		reactiveMongoOperations.save(objectToSave).awaitFirstOrDefault(null)

	override suspend fun <T> save(objectToSave: T, collectionName: String): T? =
		reactiveMongoOperations.save(objectToSave, collectionName).awaitFirstOrDefault(null)

	override suspend fun <T> save(objectToSave: suspend () -> T?): T? =
		objectToSave()?.let { save(it) }

	override suspend fun <T> save(objectToSave: suspend () -> T?, collectionName: String): T? =
		objectToSave()?.let { save(it, collectionName) }

	override suspend fun upsert(query: Query, update: Update, entityClass: Class<*>): UpdateResult? =
		reactiveMongoOperations.upsert(query, update, entityClass).awaitFirstOrDefault(null)

	override suspend fun upsert(query: Query, update: Update, collectionName: String): UpdateResult? =
		reactiveMongoOperations.upsert(query, update, collectionName).awaitFirstOrDefault(null)

	override suspend fun upsert(
		query: Query,
		update: Update,
		entityClass: Class<*>,
		collectionName: String
	): UpdateResult? =
		reactiveMongoOperations.upsert(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun updateFirst(query: Query, update: Update, entityClass: Class<*>): UpdateResult? =
		reactiveMongoOperations.updateFirst(query, update, entityClass).awaitFirstOrDefault(null)

	override suspend fun updateFirst(query: Query, update: Update, collectionName: String): UpdateResult? =
		reactiveMongoOperations.updateFirst(query, update, collectionName).awaitFirstOrDefault(null)

	override suspend fun updateFirst(
		query: Query,
		update: Update,
		entityClass: Class<*>,
		collectionName: String
	): UpdateResult? =
		reactiveMongoOperations.updateFirst(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun updateMulti(query: Query, update: Update, entityClass: Class<*>): UpdateResult? =
		reactiveMongoOperations.updateMulti(query, update, entityClass).awaitFirstOrDefault(null)

	override suspend fun updateMulti(query: Query, update: Update, collectionName: String): UpdateResult? =
		reactiveMongoOperations.updateMulti(query, update, collectionName).awaitFirstOrDefault(null)

	override suspend fun updateMulti(
		query: Query,
		update: Update,
		entityClass: Class<*>,
		collectionName: String
	): UpdateResult? =
		reactiveMongoOperations.updateMulti(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun remove(`object`: Any): DeleteResult? =
		reactiveMongoOperations.remove(`object`).awaitFirstOrDefault(null)

	override suspend fun remove(`object`: Any, collection: String): DeleteResult? =
		reactiveMongoOperations.remove(`object`, collection).awaitFirstOrDefault(null)

	override suspend fun remove(objectToRemove: suspend () -> Any?): DeleteResult? =
		objectToRemove()?.let { remove(it) }

	override suspend fun remove(objectToRemove: () -> Any?, collection: String): DeleteResult? =
		objectToRemove()?.let { remove(it, collection) }

	override suspend fun remove(query: Query, entityClass: Class<*>): DeleteResult? =
		reactiveMongoOperations.remove(query, entityClass).awaitFirstOrDefault(null)

	override suspend fun remove(query: Query, entityClass: Class<*>, collectionName: String): DeleteResult? =
		reactiveMongoOperations.remove(query, entityClass, collectionName).awaitFirstOrDefault(null)

	override suspend fun remove(query: Query, collectionName: String): DeleteResult? =
		reactiveMongoOperations.remove(query, collectionName).awaitFirstOrDefault(null)

	override suspend fun <T> findAllAndRemove(query: Query, collectionName: String): List<T> =
		reactiveMongoOperations.findAllAndRemove<T>(query, collectionName).collectList().awaitSingle()

	override suspend fun <T> findAllAndRemove(query: Query, entityClass: Class<T>): List<T> =
		reactiveMongoOperations.findAllAndRemove(query, entityClass).collectList().awaitSingle()

	override suspend fun <T> findAllAndRemove(query: Query, entityClass: Class<T>, collectionName: String): List<T> =
		reactiveMongoOperations.findAllAndRemove(query, entityClass, collectionName).collectList().awaitSingle()

	override val converter: MongoConverter
		get() = reactiveMongoOperations.converter

	override fun <T> tail(query: Query, entityClass: Class<T>): ReceiveChannel<T> =
		reactiveMongoOperations.tail(query, entityClass).openSubscription()

	override fun <T> tail(query: Query, entityClass: Class<T>, collectionName: String): ReceiveChannel<T> =
		reactiveMongoOperations.tail(query, entityClass, collectionName).openSubscription()
}