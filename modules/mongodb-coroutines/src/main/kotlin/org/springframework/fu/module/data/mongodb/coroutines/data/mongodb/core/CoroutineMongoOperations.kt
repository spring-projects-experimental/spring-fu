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
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import org.bson.Document
import org.reactivestreams.Subscription
import org.springframework.data.geo.GeoResult
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOptions
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.fu.module.data.mongodb.coroutines.data.mongodb.core.index.CoroutineIndexOperations
import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

interface CoroutineMongoOperations {
    /**
     * Returns the coroutine operations that can be performed on indexes
     *
     * @return index operations on the named collection
     */
    fun indexOps(collectionName: String): CoroutineIndexOperations

    /**
     * Returns the coroutine operations that can be performed on indexes
     *
     * @return index operations on the named collection associated with the given entity class
     */
    fun indexOps(entityClass: Class<*>): CoroutineIndexOperations

    /**
     * Execute the a MongoDB command expressed as a JSON string. This will call the method JSON.parse that is part of the
     * MongoDB driver to convert the JSON string to a DBObject. Any errors that result from executing this command will be
     * converted into Spring's DAO exception hierarchy.
     *
     * @param jsonCommand a MongoDB command expressed as a JSON string.
     * @return a result object returned by the action
     */
    suspend fun executeCommand(jsonCommand: String): Document?

    /**
     * Execute a MongoDB command. Any errors that result from executing this command will be converted into Spring's DAO
     * exception hierarchy.
     *
     * @param command a MongoDB command
     * @return a result object returned by the action
     */
    suspend fun executeCommand(command: Document): Document?

    /**
     * Execute a MongoDB command. Any errors that result from executing this command will be converted into Spring's data
     * access exception hierarchy.
     *
     * @param command a MongoDB command, must not be null.
     * @param readPreference read preferences to use, can be null.
     * @return a result object returned by the action
     */
    suspend fun executeCommand(command: Document, readPreference: ReadPreference): Document?

    /**
     * Executes a [CoroutineDatabaseCallback] translating any exceptions as necessary.
     *
     *
     * Allows for returning a result object, that is a domain object or a collection of domain objects.
     *
     * @param <T> return type
     * @param action callback object that specifies the MongoDB actions to perform on the passed in DB instance.
     * @return a result object returned by the action
    </T> */
    suspend fun <T> execute(action: CoroutineDatabaseCallback<T>): List<T>

    /**
     * Executes the given [CoroutineCollectionCallback] on the entity collection of the specified class.
     *
     *
     * Allows for returning a result object, that is a domain object or a collection of domain objects.
     *
     * @param entityClass class that determines the collection to use
     * @param <T> return type
     * @param action callback object that specifies the MongoDB action
     * @return a result object returned by the action or <tt>null</tt>
    </T> */
    suspend fun <T> execute(entityClass: Class<*>, action: CoroutineCollectionCallback<T>): List<T>

    /**
     * Executes the given [CoroutineCollectionCallback] on the collection of the given name.
     *
     *
     * Allows for returning a result object, that is a domain object or a collection of domain objects.
     *
     * @param <T> return type
     * @param collectionName the name of the collection that specifies which DBCollection instance will be passed into
     * @param action callback object that specifies the MongoDB action the callback action.
     * @return a result object returned by the action or <tt>null</tt>
    </T> */
    suspend fun <T> execute(collectionName: String, action: CoroutineCollectionCallback<T>): List<T>

    /**
     * Create an uncapped collection with a name based on the provided entity class.
     *
     * @param entityClass class that determines the collection to create
     * @return the created collection
     */
    suspend fun <T> createCollection(entityClass: Class<T>): CoroutineMongoCollection<Document>

    /**
     * Create a collection with a name based on the provided entity class using the options.
     *
     * @param entityClass class that determines the collection to create
     * @param collectionOptions options to use when creating the collection.
     * @return the created collection
     */
    suspend fun <T> createCollection(entityClass: Class<T>, collectionOptions: CollectionOptions): CoroutineMongoCollection<Document>

    /**
     * Create an uncapped collection with the provided name.
     *
     * @param collectionName name of the collection
     * @return the created collection
     */
    suspend fun createCollection(collectionName: String): CoroutineMongoCollection<Document>

    /**
     * Create a collection with the provided name and options.
     *
     * @param collectionName name of the collection
     * @param collectionOptions options to use when creating the collection.
     * @return the created collection
     */
    suspend fun createCollection(collectionName: String, collectionOptions: CollectionOptions): CoroutineMongoCollection<Document>

    /**
     * A set of collection names.
     *
     * @return Flux of collection names
     */
    suspend fun getCollectionNames(): List<String>

    /**
     * Get a collection by name, creating it if it doesn't exist.
     *
     *
     * Translate any exceptions as necessary.
     *
     * @param collectionName name of the collection
     * @return an existing collection or a newly created one.
     */
    fun getCollection(collectionName: String): CoroutineMongoCollection<Document>

    /**
     * Check to see if a collection with a name indicated by the entity class exists.
     *
     *
     * Translate any exceptions as necessary.
     *
     * @param entityClass class that determines the name of the collection
     * @return true if a collection with the given name is found, false otherwise.
     */
    suspend fun <T> collectionExists(entityClass: Class<T>): Boolean

    /**
     * Check to see if a collection with a given name exists.
     *
     *
     * Translate any exceptions as necessary.
     *
     * @param collectionName name of the collection
     * @return true if a collection with the given name is found, false otherwise.
     */
    suspend fun collectionExists(collectionName: String): Boolean

    /**
     * Drop the collection with the name indicated by the entity class.
     *
     *
     * Translate any exceptions as necessary.
     *
     * @param entityClass class that determines the collection to drop/delete.
     */
    suspend fun <T> dropCollection(entityClass: Class<T>): Unit

    /**
     * Drop the collection with the given name.
     *
     *
     * Translate any exceptions as necessary.
     *
     * @param collectionName name of the collection to drop/delete.
     */
    suspend fun dropCollection(collectionName: String): Unit

    /**
     * Query for a [ReceiveChannel] of objects of type T from the collection used by the entity class.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * If your collection does not contain a homogeneous collection of types, this operation will not be an efficient way
     * to map objects since the test for class type is done in the client and not on the server.
     *
     * @param entityClass the parametrized type of the returned [ReceiveChannel].
     * @return the converted collection
     */
    suspend fun <T> findAll(entityClass: Class<T>): List<T>

    /**
     * Query for a [ReceiveChannel] of objects of type T from the specified collection.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * If your collection does not contain a homogeneous collection of types, this operation will not be an efficient way
     * to map objects since the test for class type is done in the client and not on the server.
     *
     * @param entityClass the parametrized type of the returned [ReceiveChannel].
     * @param collectionName name of the collection to retrieve the objects from
     * @return the converted collection
     */
    suspend fun <T> findAll(entityClass: Class<T>, collectionName: String): List<T>

    /**
     * Map the results of an ad-hoc query on the collection for the entity class to a single instance of an object of the
     * specified type.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned object.
     * @return the converted object
     */
    suspend fun <T> findOne(query: Query, entityClass: Class<T>): T?

    /**
     * Map the results of an ad-hoc query on the specified collection to a single instance of an object of the specified
     * type.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned object.
     * @param collectionName name of the collection to retrieve the objects from
     * @return the converted object
     */
    suspend fun <T> findOne(query: Query, entityClass: Class<T>, collectionName: String): T?

    /**
     * Determine result of given [Query] contains at least one element. <br></br>
     * **NOTE:** Any additional support for query/field mapping, etc. is not available due to the lack of
     * domain type information. Use [.exists] to get full type specific support.
     *
     * @param query the [Query] class that specifies the criteria used to find a record.
     * @param collectionName name of the collection to check for objects.
     * @return
     */
    suspend fun exists(query: Query, collectionName: String): Boolean

    /**
     * Determine result of given [Query] contains at least one element.
     *
     * @param query the [Query] class that specifies the criteria used to find a record.
     * @param entityClass the parametrized type.
     * @return
     */
    suspend fun exists(query: Query, entityClass: Class<*>): Boolean

    /**
     * Determine result of given [Query] contains at least one element.
     *
     * @param query the [Query] class that specifies the criteria used to find a record.
     * @param entityClass the parametrized type.
     * @param collectionName name of the collection to check for objects.
     * @return
     */
    suspend fun exists(query: Query, entityClass: Class<*>, collectionName: String): Boolean

    /**
     * Map the results of an ad-hoc query on the collection for the entity class to a [ReceiveChannel] of the specified type.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned [ReceiveChannel].
     * @return the [ReceiveChannel] of converted objects
     */
    suspend fun <T> find(query: Query, entityClass: Class<T>): List<T>

    /**
     * Map the results of an ad-hoc query on the specified collection to a [ReceiveChannel] of the specified type.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned [ReceiveChannel].
     * @param collectionName name of the collection to retrieve the objects from
     * @return the [ReceiveChannel] of converted objects
     */
    suspend fun <T> find(query: Query, entityClass: Class<T>, collectionName: String): List<T>

    /**
     * Returns a document with the given id mapped onto the given class. The collection the query is ran against will be
     * derived from the given target class as well.
     *
     * @param <T>
     * @param id the id of the document to return.
     * @param entityClass the type the document shall be converted into.
     * @return the document with the given id mapped onto the given target class.
    </T> */
    suspend fun <T> findById(id: Any, entityClass: Class<T>): T?

    /**
     * Returns the document with the given id from the given collection mapped onto the given target class.
     *
     * @param id the id of the document to return
     * @param entityClass the type to convert the document to
     * @param collectionName the collection to query for the document
     * @param <T>
     * @return
    </T> */
    suspend fun <T> findById(id: Any, entityClass: Class<T>, collectionName: String): T?

    /**
     * Execute an aggregation operation.
     *
     *
     * The raw results will be mapped to the given entity class.
     *
     *
     * Aggregation streaming cannot be used with [aggregation explain][AggregationOptions.isExplain] nor with
     * [AggregationOptions.getCursorBatchSize]. Enabling explanation mode or setting batch size cause
     * [IllegalArgumentException].
     *
     * @param aggregation The [TypedAggregation] specification holding the aggregation operations. Must not be
     * null.
     * @param collectionName The name of the input collection to use for the aggregation. Must not be null.
     * @param outputType The parametrized type of the returned [ReceiveChannel]. Must not be null.
     * @return The results of the aggregation operation.
     * @throws IllegalArgumentException if `aggregation`, `collectionName` or `outputType` is
     * null.
     */
    suspend fun <O> aggregate(aggregation: TypedAggregation<*>, collectionName: String, outputType: Class<O>): List<O>

    /**
     * Execute an aggregation operation.
     *
     *
     * The raw results will be mapped to the given entity class and are returned as stream. The name of the
     * inputCollection is derived from the [aggregation input type][TypedAggregation.getInputType].
     *
     *
     * Aggregation streaming cannot be used with [aggregation explain][AggregationOptions.isExplain] nor with
     * [AggregationOptions.getCursorBatchSize]. Enabling explanation mode or setting batch size cause
     * [IllegalArgumentException].
     *
     * @param aggregation The [TypedAggregation] specification holding the aggregation operations. Must not be
     * null.
     * @param outputType The parametrized type of the returned [ReceiveChannel]. Must not be null.
     * @return The results of the aggregation operation.
     * @throws IllegalArgumentException if `aggregation` or `outputType` is null.
     */
    suspend fun <O> aggregate(aggregation: TypedAggregation<*>, outputType: Class<O>): List<O>

    /**
     * Execute an aggregation operation.
     *
     *
     * The raw results will be mapped to the given `ouputType`. The name of the inputCollection is derived from the
     * `inputType`.
     *
     *
     * Aggregation streaming cannot be used with [aggregation explain][AggregationOptions.isExplain] nor with
     * [AggregationOptions.getCursorBatchSize]. Enabling explanation mode or setting batch size cause
     * [IllegalArgumentException].
     *
     * @param aggregation The [Aggregation] specification holding the aggregation operations. Must not be
     * null.
     * @param inputType the inputType where the aggregation operation will read from. Must not be null.
     * @param outputType The parametrized type of the returned [ReceiveChannel]. Must not be null.
     * @return The results of the aggregation operation.
     * @throws IllegalArgumentException if `aggregation`, `inputType` or `outputType` is
     * null.
     */
    suspend fun <O> aggregate(aggregation: Aggregation, inputType: Class<*>, outputType: Class<O>): List<O>

    /**
     * Execute an aggregation operation.
     *
     *
     * The raw results will be mapped to the given entity class.
     *
     *
     * Aggregation streaming cannot be used with [aggregation explain][AggregationOptions.isExplain] nor with
     * [AggregationOptions.getCursorBatchSize]. Enabling explanation mode or setting batch size cause
     * [IllegalArgumentException].
     *
     * @param aggregation The [Aggregation] specification holding the aggregation operations. Must not be
     * null.
     * @param collectionName the collection where the aggregation operation will read from. Must not be null or
     * empty.
     * @param outputType The parametrized type of the returned [ReceiveChannel]. Must not be null.
     * @return The results of the aggregation operation.
     * @throws IllegalArgumentException if `aggregation`, `collectionName` or `outputType` is
     * null.
     */
    suspend fun <O> aggregate(aggregation: Aggregation, collectionName: String, outputType: Class<O>): List<O>

    /**
     * Returns [ReceiveChannel] of [GeoResult] for all entities matching the given [NearQuery]. Will consider
     * entity mapping information to determine the collection the query is ran against. Note, that MongoDB limits the
     * number of results by default. Make sure to add an explicit limit to the [NearQuery] if you expect a
     * particular number of results.
     *
     * @param near must not be null.
     * @param entityClass must not be null.
     * @return
     */
    suspend fun <T> geoNear(near: NearQuery, entityClass: Class<T>): List<GeoResult<T>>

    /**
     * Returns [ReceiveChannel] of [GeoResult] for all entities matching the given [NearQuery]. Note, that MongoDB
     * limits the number of results by default. Make sure to add an explicit limit to the [NearQuery] if you expect
     * a particular number of results.
     *
     * @param near must not be null.
     * @param entityClass must not be null.
     * @param collectionName the collection to trigger the query against. If no collection name is given the entity class
     * will be inspected.
     * @return
     */
    suspend fun <T> geoNear(near: NearQuery, entityClass: Class<T>, collectionName: String): List<GeoResult<T>>

    /**
     * Triggers [findAndModify <a></a>
     * to apply provided [Update] on documents matching [Criteria] of given [Query].
     *
     * @param query the [Query] class that specifies the [Criteria] used to find a record and also an optional
     * fields specification.
     * @param update the [Update] to apply on matching documents.
 * @param entityClass the parametrized type.
 * @return
](https://docs.mongodb.org/manual/reference/method/db.collection.findAndModify/) */
    suspend fun <T> findAndModify(query: Query, update: Update, entityClass: Class<T>): T?

    /**
     * Triggers [findAndModify <a></a>
     * to apply provided [Update] on documents matching [Criteria] of given [Query].
     *
     * @param query the [Query] class that specifies the [Criteria] used to find a record and also an optional
     * fields specification.
     * @param update the [Update] to apply on matching documents.
 * @param entityClass the parametrized type.
 * @param collectionName the collection to query.
 * @return
](https://docs.mongodb.org/manual/reference/method/db.collection.findAndModify/) */
    suspend fun <T> findAndModify(query: Query, update: Update, entityClass: Class<T>, collectionName: String): T?

    /**
     * Triggers [findAndModify <a></a>
     * to apply provided [Update] on documents matching [Criteria] of given [Query] taking
     * [FindAndModifyOptions] into account.
     *
     * @param query the [Query] class that specifies the [Criteria] used to find a record and also an optional
     * fields specification.
     * @param update the [Update] to apply on matching documents.
     * @param options the [FindAndModifyOptions] holding additional information.
 * @param entityClass the parametrized type.
 * @return
](https://docs.mongodb.org/manual/reference/method/db.collection.findAndModify/) */
    suspend fun <T> findAndModify(query: Query, update: Update, options: FindAndModifyOptions, entityClass: Class<T>): T?

    /**
     * Triggers [findAndModify <a></a>
     * to apply provided [Update] on documents matching [Criteria] of given [Query] taking
     * [FindAndModifyOptions] into account.
     *
     * @param query the [Query] class that specifies the [Criteria] used to find a record and also an optional
     * fields specification.
     * @param update the [Update] to apply on matching documents.
     * @param options the [FindAndModifyOptions] holding additional information.
 * @param entityClass the parametrized type.
 * @param collectionName the collection to query.
 * @return
](https://docs.mongodb.org/manual/reference/method/db.collection.findAndModify/) */
    suspend fun <T> findAndModify(query: Query, update: Update, options: FindAndModifyOptions, entityClass: Class<T>,
								  collectionName: String): T?

    /**
     * Map the results of an ad-hoc query on the collection for the entity type to a single instance of an object of the
     * specified type. The first document that matches the query is returned and also removed from the collection in the
     * database.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned object.
     * @return the converted object
     */
    suspend fun <T> findAndRemove(query: Query, entityClass: Class<T>): T?

    /**
     * Map the results of an ad-hoc query on the specified collection to a single instance of an object of the specified
     * type. The first document that matches the query is returned and also removed from the collection in the database.
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned object.
     * @param collectionName name of the collection to retrieve the objects from.
     * @return the converted object
     */
    suspend fun <T> findAndRemove(query: Query, entityClass: Class<T>, collectionName: String): T?

    /**
     * Returns the number of documents for the given [Query] by querying the collection of the given entity class.
     *
     * @param query
     * @param entityClass must not be null.
     * @return
     */
    suspend fun count(query: Query, entityClass: Class<*>): Long

    /**
     * Returns the number of documents for the given [Query] querying the given collection. The given [Query]
     * must solely consist of document field references as we lack type information to map potential property references
     * onto document fields. Use [.count] to get full type specific support.
     *
     * @param query
     * @param collectionName must not be null or empty.
     * @return
     * @see .count
     */
    suspend fun count(query: Query, collectionName: String): Long

    /**
     * Returns the number of documents for the given [Query] by querying the given collection using the given entity
     * class to map the given [Query].
     *
     * @param query
     * @param entityClass must not be null.
     * @param collectionName must not be null or empty.
     * @return
     */
    suspend fun count(query: Query, entityClass: Class<*>, collectionName: String): Long

    /**
     * Insert the object into the collection for the entity type of the object to save.
     *
     *
     * The object is converted to the MongoDB native representation using an instance of {@see MongoConverter}.
     *
     *
     * If you object has an "Id' property, it will be set with the generated Id from MongoDB. If your Id property is a
     * String then MongoDB ObjectId will be used to populate that string. Otherwise, the conversion from ObjectId to your
     * property type will be handled by Spring's BeanWrapper class that leverages Type Conversion API. See
     * [
 * Spring's Type Conversion"](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html#core-convert) for more details.
     *
     *
     *
     *
     * Insert is used to initially store the object into the database. To update an existing object use the save method.
     *
     * @param objectToSave the object to store in the collection.
     * @return
     */
    suspend fun <T> insert(objectToSave: T): T?

    /**
     * Insert the object into the specified collection.
     *
     *
     * The object is converted to the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * Insert is used to initially store the object into the database. To update an existing object use the save method.
     *
     * @param objectToSave the object to store in the collection
     * @param collectionName name of the collection to store the object in
     * @return
     */
    suspend fun <T> insert(objectToSave: T, collectionName: String): T?

    /**
     * Insert a Collection of objects into a collection in a single batch write to the database.
     *
     * @param batchToSave the batch of objects to save.
     * @param entityClass class that determines the collection to use
     * @return
     */
    suspend fun <T> insert(batchToSave: Collection<T>, entityClass: Class<*>): List<T>

    /**
     * Insert a batch of objects into the specified collection in a single batch write to the database.
     *
     * @param batchToSave the list of objects to save.
     * @param collectionName name of the collection to store the object in
     * @return
     */
    suspend fun <T> insert(batchToSave: Collection<T>, collectionName: String): List<T>

    /**
     * Insert a mixed Collection of objects into a database collection determining the collection name to use based on the
     * class.
     *
     * @param objectsToSave the list of objects to save.
     * @return
     */
    suspend fun <T> insertAll(objectsToSave: Collection<T>): List<T>

    /**
     * Insert the object into the collection for the entity type of the object to save.
     *
     *
     * The object is converted to the MongoDB native representation using an instance of {@see MongoConverter}.
     *
     *
     * If you object has an "Id' property, it will be set with the generated Id from MongoDB. If your Id property is a
     * String then MongoDB ObjectId will be used to populate that string. Otherwise, the conversion from ObjectId to your
     * property type will be handled by Spring's BeanWrapper class that leverages Type Conversion API. See
     * [
 * Spring's Type Conversion"](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html#core-convert) for more details.
     *
     *
     *
     *
     * Insert is used to initially store the object into the database. To update an existing object use the save method.
     *
     * @param objectToSave the object to store in the collection.
     * @return
     */
    suspend fun <T> insert(objectToSave: suspend () -> T?): T?

    /**
     * Insert a Collection of objects into a collection in a single batch write to the database.
     *
     * @param batchToSave the publisher which provides objects to save.
     * @param entityClass class that determines the collection to use
     * @return
     */
    suspend fun <T> insertAll(batchToSave: suspend () -> Collection<T>, entityClass: Class<*>): List<T>

    /**
     * Insert objects into the specified collection in a single batch write to the database.
     *
     * @param batchToSave the publisher which provides objects to save.
     * @param collectionName name of the collection to store the object in
     * @return
     */
    suspend fun <T> insertAll(batchToSave: suspend () -> Collection<T>, collectionName: String): List<T>

    /**
     * Insert a mixed Collection of objects into a database collection determining the collection name to use based on the
     * class.
     *
     * @param objectsToSave the publisher which provides objects to save.
     * @return
     */
    suspend fun <T> insertAll(objectsToSave: suspend () -> Collection<T>): List<T>

    /**
     * Save the object to the collection for the entity type of the object to save. This will perform an insert if the
     * object is not already present, that is an 'upsert'.
     *
     *
     * The object is converted to the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * If you object has an "Id' property, it will be set with the generated Id from MongoDB. If your Id property is a
     * String then MongoDB ObjectId will be used to populate that string. Otherwise, the conversion from ObjectId to your
     * property type will be handled by Spring's BeanWrapper class that leverages Type Conversion API. See
     * [
 * Spring's Type Conversion"](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html#core-convert) for more details.
     *
     * @param objectToSave the object to store in the collection
     * @return
     */
    suspend fun <T> save(objectToSave: T): T?

    /**
     * Save the object to the specified collection. This will perform an insert if the object is not already present, that
     * is an 'upsert'.
     *
     *
     * The object is converted to the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * If you object has an "Id' property, it will be set with the generated Id from MongoDB. If your Id property is a
     * String then MongoDB ObjectId will be used to populate that string. Otherwise, the conversion from ObjectId to your
     * property type will be handled by Spring's BeanWrapper class that leverages Type Conversion API. See <a http:></a>//docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html#core-convert">Spring's
     * Type Conversion" for more details.
     *
     * @param objectToSave the object to store in the collection
     * @param collectionName name of the collection to store the object in
     * @return
     */
    suspend fun <T> save(objectToSave: T, collectionName: String): T?

    /**
     * Save the object to the collection for the entity type of the object to save. This will perform an insert if the
     * object is not already present, that is an 'upsert'.
     *
     *
     * The object is converted to the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * If you object has an "Id' property, it will be set with the generated Id from MongoDB. If your Id property is a
     * String then MongoDB ObjectId will be used to populate that string. Otherwise, the conversion from ObjectId to your
     * property type will be handled by Spring's BeanWrapper class that leverages Type Conversion API. See
     * [
 * Spring's Type Conversion"](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html#core-convert) for more details.
     *
     * @param objectToSave the object to store in the collection
     * @return
     */
    suspend fun <T> save(objectToSave: suspend () -> T?): T?

    /**
     * Save the object to the specified collection. This will perform an insert if the object is not already present, that
     * is an 'upsert'.
     *
     *
     * The object is converted to the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * If you object has an "Id' property, it will be set with the generated Id from MongoDB. If your Id property is a
     * String then MongoDB ObjectId will be used to populate that string. Otherwise, the conversion from ObjectId to your
     * property type will be handled by Spring's BeanWrapper class that leverages Type Conversion API. See <a http:></a>//docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html#core-convert">Spring's
     * Type Conversion" for more details.
     *
     * @param objectToSave the object to store in the collection
     * @param collectionName name of the collection to store the object in
     * @return
     */
    suspend fun <T> save(objectToSave: suspend () -> T?, collectionName: String): T?

    /**
     * Performs an upsert. If no document is found that matches the query, a new document is created and inserted by
     * combining the query document and the update document.
     *
     * @param query the query document that specifies the criteria used to select a record to be upserted
     * @param update the update document that contains the updated object or $ operators to manipulate the existing object
     * @param entityClass class that determines the collection to use
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun upsert(query: Query, update: Update, entityClass: Class<*>): UpdateResult?

    /**
     * Performs an upsert. If no document is found that matches the query, a new document is created and inserted by
     * combining the query document and the update document. <br></br>
     * **NOTE:** Any additional support for field mapping, versions, etc. is not available due to the lack of
     * domain type information. Use [.upsert] to get full type specific support.
     *
     * @param query the query document that specifies the criteria used to select a record to be updated
     * @param update the update document that contains the updated object or $ operators to manipulate the existing
     * object.
     * @param collectionName name of the collection to update the object in
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun upsert(query: Query, update: Update, collectionName: String): UpdateResult?

    /**
     * Performs an upsert. If no document is found that matches the query, a new document is created and inserted by
     * combining the query document and the update document.
     *
     * @param query the query document that specifies the criteria used to select a record to be upserted
     * @param update the update document that contains the updated object or $ operators to manipulate the existing object
     * @param entityClass class of the pojo to be operated on
     * @param collectionName name of the collection to update the object in
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun upsert(query: Query, update: Update, entityClass: Class<*>, collectionName: String): UpdateResult?

    /**
     * Updates the first object that is found in the collection of the entity class that matches the query document with
     * the provided update document.
     *
     * @param query the query document that specifies the criteria used to select a record to be updated
     * @param update the update document that contains the updated object or $ operators to manipulate the existing
     * object.
     * @param entityClass class that determines the collection to use
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun updateFirst(query: Query, update: Update, entityClass: Class<*>): UpdateResult?

    /**
     * Updates the first object that is found in the specified collection that matches the query document criteria with
     * the provided updated document. <br></br>
     * **NOTE:** Any additional support for field mapping, versions, etc. is not available due to the lack of
     * domain type information. Use [.updateFirst] to get full type specific support.
     *
     * @param query the query document that specifies the criteria used to select a record to be updated
     * @param update the update document that contains the updated object or $ operators to manipulate the existing
     * object.
     * @param collectionName name of the collection to update the object in
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun updateFirst(query: Query, update: Update, collectionName: String): UpdateResult?

    /**
     * Updates the first object that is found in the specified collection that matches the query document criteria with
     * the provided updated document. <br></br>
     * **NOTE:** Any additional support for field mapping, versions, etc. is not available due to the lack of
     * domain type information. Use [.updateFirst] to get full type specific support.
     *
     * @param query the query document that specifies the criteria used to select a record to be updated
     * @param update the update document that contains the updated object or $ operators to manipulate the existing
     * object.
     * @param entityClass class of the pojo to be operated on
     * @param collectionName name of the collection to update the object in
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun updateFirst(query: Query, update: Update, entityClass: Class<*>, collectionName: String): UpdateResult?

    /**
     * Updates all objects that are found in the collection for the entity class that matches the query document criteria
     * with the provided updated document.
     *
     * @param query the query document that specifies the criteria used to select a record to be updated
     * @param update the update document that contains the updated object or $ operators to manipulate the existing
     * object.
     * @param entityClass class that determines the collection to use
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun updateMulti(query: Query, update: Update, entityClass: Class<*>): UpdateResult?

    /**
     * Updates all objects that are found in the specified collection that matches the query document criteria with the
     * provided updated document. <br></br>
     * **NOTE:** Any additional support for field mapping, versions, etc. is not available due to the lack of
     * domain type information. Use [.updateMulti] to get full type specific support.
     *
     * @param query the query document that specifies the criteria used to select a record to be updated
     * @param update the update document that contains the updated object or $ operators to manipulate the existing
     * object.
     * @param collectionName name of the collection to update the object in
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun updateMulti(query: Query, update: Update, collectionName: String): UpdateResult?

    /**
     * Updates all objects that are found in the collection for the entity class that matches the query document criteria
     * with the provided updated document.
     *
     * @param query the query document that specifies the criteria used to select a record to be updated
     * @param update the update document that contains the updated object or $ operators to manipulate the existing
     * object.
     * @param entityClass class of the pojo to be operated on
     * @param collectionName name of the collection to update the object in
     * @return the WriteResult which lets you access the results of the previous write.
     */
    suspend fun updateMulti(query: Query, update: Update, entityClass: Class<*>, collectionName: String): UpdateResult?

    /**
     * Remove the given object from the collection by id.
     *
     * @param object
     * @return
     */
    suspend fun remove(`object`: Any): DeleteResult?

    /**
     * Removes the given object from the given collection.
     *
     * @param object
     * @param collection must not be null or empty.
     */
    suspend fun remove(`object`: Any, collection: String): DeleteResult?

    /**
     * Remove the given object from the collection by id.
     *
     * @param objectToRemove
     * @return
     */
    suspend fun remove(objectToRemove: suspend () -> Any?): DeleteResult?

    /**
     * Removes the given object from the given collection.
     *
     * @param objectToRemove
     * @param collection must not be null or empty.
     * @return
     */
    suspend fun remove(objectToRemove: () -> Any?, collection: String): DeleteResult?

    /**
     * Remove all documents that match the provided query document criteria from the the collection used to store the
     * entityClass. The Class parameter is also used to help convert the Id of the object if it is present in the query.
     *
     * @param query
     * @param entityClass
     * @return
     */
    suspend fun remove(query: Query, entityClass: Class<*>): DeleteResult?

    /**
     * Remove all documents that match the provided query document criteria from the the collection used to store the
     * entityClass. The Class parameter is also used to help convert the Id of the object if it is present in the query.
     *
     * @param query
     * @param entityClass
     * @param collectionName
     * @return
     */
    suspend fun remove(query: Query, entityClass: Class<*>, collectionName: String): DeleteResult?

    /**
     * Remove all documents from the specified collection that match the provided query document criteria. There is no
     * conversion/mapping done for any criteria using the id field. <br></br>
     * **NOTE:** Any additional support for field mapping is not available due to the lack of domain type
     * information. Use [.remove] to get full type specific support.
     *
     * @param query the query document that specifies the criteria used to remove a record
     * @param collectionName name of the collection where the objects will removed
     */
    suspend fun remove(query: Query, collectionName: String): DeleteResult?

    /**
     * Returns and removes all documents form the specified collection that match the provided query. <br></br>
     * **NOTE:** Any additional support for field mapping is not available due to the lack of domain type
     * information. Use [.findAllAndRemove] to get full type specific support.
     *
     * @param query
     * @param collectionName
     * @return
     */
    suspend fun <T> findAllAndRemove(query: Query, collectionName: String): List<T>

    /**
     * Returns and removes all documents matching the given query form the collection used to store the entityClass.
     *
     * @param query
     * @param entityClass
     * @return
     */
    suspend fun <T> findAllAndRemove(query: Query, entityClass: Class<T>): List<T>

    /**
     * Returns and removes all documents that match the provided query document criteria from the the collection used to
     * store the entityClass. The Class parameter is also used to help convert the Id of the object if it is present in
     * the query.
     *
     * @param query
     * @param entityClass
     * @param collectionName
     * @return
     */
    suspend fun <T> findAllAndRemove(query: Query, entityClass: Class<T>, collectionName: String): List<T>

    /**
     * Map the results of an ad-hoc query on the collection for the entity class to a stream of objects of the specified
     * type. The stream uses a [tailable][com.mongodb.CursorType.TailableAwait] cursor that may be an infinite
     * stream. The stream will not be completed unless the [org.reactivestreams.Subscription] is
     * [canceled][Subscription.cancel].
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned [ReceiveChannel].
     * @return the [ReceiveChannel] of converted objects
     */
    fun <T> tail(query: Query, entityClass: Class<T>): ReceiveChannel<T>

    /**
     * Map the results of an ad-hoc query on the collection for the entity class to a stream of objects of the specified
     * type. The stream uses a [tailable][com.mongodb.CursorType.TailableAwait] cursor that may be an infinite
     * stream. The stream will not be completed unless the [org.reactivestreams.Subscription] is
     * [canceled][Subscription.cancel].
     *
     *
     * The object is converted from the MongoDB native representation using an instance of {@see MongoConverter}. Unless
     * configured otherwise, an instance of [MappingMongoConverter] will be used.
     *
     *
     * The query is specified as a [Query] which can be created either using the [BasicQuery] or the more
     * feature rich [Query].
     *
     * @param query the query class that specifies the criteria used to find a record and also an optional fields
     * specification
     * @param entityClass the parametrized type of the returned [ReceiveChannel].
     * @param collectionName name of the collection to retrieve the objects from
     * @return the [ReceiveChannel] of converted objects
     */
    fun <T> tail(query: Query, entityClass: Class<T>, collectionName: String): ReceiveChannel<T>

    /**
     * Returns the underlying [MongoConverter].
     *
     * @return
     */
    val converter: MongoConverter

    val reactiveMongoOperations: ReactiveMongoOperations
}