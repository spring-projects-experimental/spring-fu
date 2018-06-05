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

package org.springframework.fu.module.data.mongodb.coroutines.data.mongodb.core

import com.mongodb.ReadPreference
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.fu.module.data.mongodb.coroutines.CoroutineMongoCollection
import org.springframework.fu.module.data.mongodb.coroutines.asCoroutineMongoCollection
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.reactive.awaitFirst
import kotlinx.coroutines.experimental.reactive.awaitFirstOrDefault
import kotlinx.coroutines.experimental.reactive.awaitLast
import kotlinx.coroutines.experimental.reactive.awaitSingle
import kotlinx.coroutines.experimental.reactive.openSubscription
import org.bson.Document
import org.springframework.data.geo.GeoResult
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.fu.module.data.mongodb.coroutines.data.mongodb.core.index.CoroutineIndexOperations
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

open class CoroutineMongoTemplate(
    override val reactiveMongoOperations: ReactiveMongoOperations
): CoroutineMongoOperations {
    override fun indexOps(collectionName: String): CoroutineIndexOperations =
			DefaultCoroutineIndexOperations(reactiveMongoOperations.indexOps(collectionName))

    override fun indexOps(entityClass: Class<*>): CoroutineIndexOperations =
			DefaultCoroutineIndexOperations(reactiveMongoOperations.indexOps(entityClass))

    suspend override fun executeCommand(jsonCommand: String): Document? =
            reactiveMongoOperations.executeCommand(jsonCommand).awaitFirstOrDefault(null)

    suspend override fun executeCommand(command: Document): Document? =
            reactiveMongoOperations.executeCommand(command).awaitFirstOrDefault(null)

    suspend override fun executeCommand(command: Document, readPreference: ReadPreference): Document? =
            reactiveMongoOperations.executeCommand(command, readPreference).awaitFirstOrDefault(null)

    suspend override fun <T> execute(action: CoroutineDatabaseCallback<T>): List<T> =
            reactiveMongoOperations.execute(action.reactiveDatabaseCallback).collectList().awaitSingle()

    suspend override fun <T> execute(entityClass: Class<*>, action: CoroutineCollectionCallback<T>): List<T> =
            reactiveMongoOperations.execute(entityClass, action.reactiveCollectionCallback).collectList().awaitSingle()

    suspend override fun <T> execute(collectionName: String, action: CoroutineCollectionCallback<T>): List<T> =
            reactiveMongoOperations.execute(collectionName, action.reactiveCollectionCallback).collectList().awaitSingle()

    suspend override fun <T> createCollection(entityClass: Class<T>): CoroutineMongoCollection<Document> =
            reactiveMongoOperations.createCollection(entityClass).awaitFirstOrDefault(null).asCoroutineMongoCollection()

    suspend override fun <T> createCollection(entityClass: Class<T>, collectionOptions: CollectionOptions): CoroutineMongoCollection<Document> =
            reactiveMongoOperations.createCollection(entityClass, collectionOptions).awaitFirstOrDefault(null).asCoroutineMongoCollection()

    suspend override fun createCollection(collectionName: String): CoroutineMongoCollection<Document> =
            reactiveMongoOperations.createCollection(collectionName).awaitFirstOrDefault(null).asCoroutineMongoCollection()

    suspend override fun createCollection(collectionName: String, collectionOptions: CollectionOptions): CoroutineMongoCollection<Document> =
            reactiveMongoOperations.createCollection(collectionName, collectionOptions).awaitFirstOrDefault(null).asCoroutineMongoCollection()

    suspend override fun getCollectionNames(): List<String> =
            reactiveMongoOperations.collectionNames.collectList().awaitSingle()

    override fun getCollection(collectionName: String): CoroutineMongoCollection<Document> =
            reactiveMongoOperations.getCollection(collectionName).asCoroutineMongoCollection()

    suspend override fun <T> collectionExists(entityClass: Class<T>): Boolean =
            reactiveMongoOperations.collectionExists(entityClass).awaitFirst()

    suspend override fun collectionExists(collectionName: String): Boolean =
            reactiveMongoOperations.collectionExists(collectionName).awaitFirst()

    suspend override fun <T> dropCollection(entityClass: Class<T>) {
        reactiveMongoOperations.dropCollection(entityClass).awaitLast()
    }

    suspend override fun dropCollection(collectionName: String) {
        reactiveMongoOperations.dropCollection(collectionName).awaitLast()
    }

    suspend override fun <T> findAll(entityClass: Class<T>): List<T> =
            reactiveMongoOperations.findAll(entityClass).collectList().awaitSingle()

    suspend override fun <T> findAll(entityClass: Class<T>, collectionName: String): List<T> =
            reactiveMongoOperations.findAll(entityClass, collectionName).collectList().awaitSingle()

    suspend override fun <T> findOne(query: Query, entityClass: Class<T>): T? =
            reactiveMongoOperations.findOne(query, entityClass).awaitFirstOrDefault(null)

    suspend override fun <T> findOne(query: Query, entityClass: Class<T>, collectionName: String): T? =
            reactiveMongoOperations.findOne(query, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun exists(query: Query, collectionName: String): Boolean =
            reactiveMongoOperations.exists(query, collectionName).awaitFirst()

    suspend override fun exists(query: Query, entityClass: Class<*>): Boolean =
            reactiveMongoOperations.exists(query, entityClass).awaitFirst()

    suspend override fun exists(query: Query, entityClass: Class<*>, collectionName: String): Boolean =
            reactiveMongoOperations.exists(query, entityClass, collectionName).awaitFirst()

    suspend override fun <T> find(query: Query, entityClass: Class<T>): List<T> =
            reactiveMongoOperations.find(query, entityClass).collectList().awaitSingle()

    suspend override fun <T> find(query: Query, entityClass: Class<T>, collectionName: String): List<T> =
            reactiveMongoOperations.find(query, entityClass, collectionName).collectList().awaitSingle()

    suspend override fun <T> findById(id: Any, entityClass: Class<T>): T? =
            reactiveMongoOperations.findById(id, entityClass).awaitFirstOrDefault(null)

    suspend override fun <T> findById(id: Any, entityClass: Class<T>, collectionName: String): T? =
            reactiveMongoOperations.findById(id, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun <O> aggregate(aggregation: TypedAggregation<*>, collectionName: String, outputType: Class<O>): List<O> =
            reactiveMongoOperations.aggregate(aggregation, collectionName, outputType).collectList().awaitSingle()

    suspend override fun <O> aggregate(aggregation: TypedAggregation<*>, outputType: Class<O>): List<O> =
            reactiveMongoOperations.aggregate(aggregation, outputType).collectList().awaitSingle()

    suspend override fun <O> aggregate(aggregation: Aggregation, inputType: Class<*>, outputType: Class<O>): List<O> =
            reactiveMongoOperations.aggregate(aggregation, inputType, outputType).collectList().awaitSingle()

    suspend override fun <O> aggregate(aggregation: Aggregation, collectionName: String, outputType: Class<O>): List<O> =
            reactiveMongoOperations.aggregate(aggregation, collectionName, outputType).collectList().awaitSingle()

    suspend override fun <T> geoNear(near: NearQuery, entityClass: Class<T>): List<GeoResult<T>> =
            reactiveMongoOperations.geoNear(near, entityClass).collectList().awaitSingle()

    suspend override fun <T> geoNear(near: NearQuery, entityClass: Class<T>, collectionName: String): List<GeoResult<T>> =
            reactiveMongoOperations.geoNear(near, entityClass, collectionName).collectList().awaitSingle()

    suspend override fun <T> findAndModify(query: Query, update: Update, entityClass: Class<T>): T? =
            reactiveMongoOperations.findAndModify(query, update, entityClass).awaitFirstOrDefault(null)

    suspend override fun <T> findAndModify(query: Query, update: Update, entityClass: Class<T>, collectionName: String): T? =
            reactiveMongoOperations.findAndModify(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun <T> findAndModify(query: Query, update: Update, options: FindAndModifyOptions, entityClass: Class<T>): T? =
            reactiveMongoOperations.findAndModify(query, update, options, entityClass).awaitFirstOrDefault(null)

    suspend override fun <T> findAndModify(query: Query, update: Update, options: FindAndModifyOptions, entityClass: Class<T>, collectionName: String): T? =
            reactiveMongoOperations.findAndModify(query, update, options, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun <T> findAndRemove(query: Query, entityClass: Class<T>): T? =
            reactiveMongoOperations.findAndRemove(query, entityClass).awaitFirstOrDefault(null)

    suspend override fun <T> findAndRemove(query: Query, entityClass: Class<T>, collectionName: String): T? =
            reactiveMongoOperations.findAndRemove(query, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun count(query: Query, entityClass: Class<*>): Long =
            reactiveMongoOperations.count(query, entityClass).awaitFirst()

    suspend override fun count(query: Query, collectionName: String): Long =
            reactiveMongoOperations.count(query, collectionName).awaitFirst()

    suspend override fun count(query: Query, entityClass: Class<*>, collectionName: String): Long =
            reactiveMongoOperations.count(query, entityClass, collectionName).awaitFirst()

    suspend override fun <T> insert(objectToSave: T): T? =
            reactiveMongoOperations.insert(objectToSave).awaitFirstOrDefault(null)

    suspend override fun <T> insert(objectToSave: T, collectionName: String): T? =
            reactiveMongoOperations.insert(objectToSave, collectionName).awaitFirstOrDefault(null)

    suspend override fun <T> insert(batchToSave: Collection<T>, entityClass: Class<*>): List<T> =
            reactiveMongoOperations.insert(batchToSave, entityClass).collectList().awaitSingle()

    suspend override fun <T> insert(batchToSave: Collection<T>, collectionName: String): List<T> =
            reactiveMongoOperations.insert(batchToSave, collectionName).collectList().awaitSingle()

    suspend override fun <T> insertAll(objectsToSave: Collection<T>): List<T> =
            reactiveMongoOperations.insertAll(objectsToSave).collectList().awaitSingle()

    suspend override fun <T> insert(objectToSave: suspend () -> T?): T? =
            objectToSave()?.let {
                reactiveMongoOperations.insert(it).awaitFirstOrDefault(null)
            }

    suspend override fun <T> insertAll(batchToSave: suspend () -> Collection<T>, entityClass: Class<*>): List<T> =
            insert(batchToSave(), entityClass)

    suspend override fun <T> insertAll(batchToSave: suspend () -> Collection<T>, collectionName: String): List<T> =
            insert(batchToSave(), collectionName)

    suspend override fun <T> insertAll(objectsToSave: suspend () -> Collection<T>): List<T> =
            insertAll(objectsToSave())

    suspend override fun <T> save(objectToSave: T): T? =
            reactiveMongoOperations.save(objectToSave).awaitFirstOrDefault(null)

    suspend override fun <T> save(objectToSave: T, collectionName: String): T? =
            reactiveMongoOperations.save(objectToSave, collectionName).awaitFirstOrDefault(null)

    suspend override fun <T> save(objectToSave: suspend () -> T?): T? =
            objectToSave()?.let { save(it) }

    suspend override fun <T> save(objectToSave: suspend () -> T?, collectionName: String): T? =
            objectToSave()?.let { save(it, collectionName) }

    suspend override fun upsert(query: Query, update: Update, entityClass: Class<*>): UpdateResult? =
            reactiveMongoOperations.upsert(query, update, entityClass).awaitFirstOrDefault(null)

    suspend override fun upsert(query: Query, update: Update, collectionName: String): UpdateResult? =
            reactiveMongoOperations.upsert(query, update, collectionName).awaitFirstOrDefault(null)

    suspend override fun upsert(query: Query, update: Update, entityClass: Class<*>, collectionName: String): UpdateResult? =
            reactiveMongoOperations.upsert(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun updateFirst(query: Query, update: Update, entityClass: Class<*>): UpdateResult? =
            reactiveMongoOperations.updateFirst(query, update, entityClass).awaitFirstOrDefault(null)

    suspend override fun updateFirst(query: Query, update: Update, collectionName: String): UpdateResult? =
            reactiveMongoOperations.updateFirst(query, update, collectionName).awaitFirstOrDefault(null)

    suspend override fun updateFirst(query: Query, update: Update, entityClass: Class<*>, collectionName: String): UpdateResult? =
            reactiveMongoOperations.updateFirst(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun updateMulti(query: Query, update: Update, entityClass: Class<*>): UpdateResult? =
            reactiveMongoOperations.updateMulti(query, update, entityClass).awaitFirstOrDefault(null)

    suspend override fun updateMulti(query: Query, update: Update, collectionName: String): UpdateResult? =
            reactiveMongoOperations.updateMulti(query, update, collectionName).awaitFirstOrDefault(null)

    suspend override fun updateMulti(query: Query, update: Update, entityClass: Class<*>, collectionName: String): UpdateResult? =
            reactiveMongoOperations.updateMulti(query, update, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun remove(`object`: Any): DeleteResult? =
            reactiveMongoOperations.remove(`object`).awaitFirstOrDefault(null)

    suspend override fun remove(`object`: Any, collection: String): DeleteResult? =
            reactiveMongoOperations.remove(`object`, collection).awaitFirstOrDefault(null)

    suspend override fun remove(objectToRemove: suspend () -> Any?): DeleteResult? =
            objectToRemove()?.let { remove(it) }

    suspend override fun remove(objectToRemove: () -> Any?, collection: String): DeleteResult? =
            objectToRemove()?.let { remove(it, collection) }

    suspend override fun remove(query: Query, entityClass: Class<*>): DeleteResult? =
            reactiveMongoOperations.remove(query, entityClass).awaitFirstOrDefault(null)

    suspend override fun remove(query: Query, entityClass: Class<*>, collectionName: String): DeleteResult? =
            reactiveMongoOperations.remove(query, entityClass, collectionName).awaitFirstOrDefault(null)

    suspend override fun remove(query: Query, collectionName: String): DeleteResult? =
            reactiveMongoOperations.remove(query, collectionName).awaitFirstOrDefault(null)

    suspend override fun <T> findAllAndRemove(query: Query, collectionName: String): List<T> =
            reactiveMongoOperations.findAllAndRemove<T>(query, collectionName).collectList().awaitSingle()

    suspend override fun <T> findAllAndRemove(query: Query, entityClass: Class<T>): List<T> =
            reactiveMongoOperations.findAllAndRemove(query, entityClass).collectList().awaitSingle()

    suspend override fun <T> findAllAndRemove(query: Query, entityClass: Class<T>, collectionName: String): List<T> =
            reactiveMongoOperations.findAllAndRemove(query, entityClass, collectionName).collectList().awaitSingle()

    override val converter: MongoConverter
        get() = reactiveMongoOperations.converter

    override fun <T> tail(query: Query, entityClass: Class<T>): ReceiveChannel<T> =
            reactiveMongoOperations.tail(query, entityClass).openSubscription()

    override fun <T> tail(query: Query, entityClass: Class<T>, collectionName: String): ReceiveChannel<T> =
            reactiveMongoOperations.tail(query, entityClass, collectionName).openSubscription()
}