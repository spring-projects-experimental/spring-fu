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

package org.springframework.fu.module.data.mongodb.coroutines.data.repository.coroutine

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface CoroutineCrudRepository<T, ID>: Repository<T, ID> {
    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be null.
     * @return the saved entity.
     * @throws IllegalArgumentException in case the given `entity` is null.
     */
    suspend fun <S : T> save(entity: S): S

    /**
     * Saves all given entities.
     *
     * @param entities must not be null.
     * @return saved entities.
     * @throws IllegalArgumentException in case the given [Iterable] `entities` is null.
     */
    suspend fun <S : T> saveAll(entities: Iterable<S>): List<S>

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be null.
     * @return the entity with the given id or null if none found.
     * @throws IllegalArgumentException in case the given `id` is null.
     */
    suspend fun findById(id: ID): T?

    /**
     * Returns whether an entity with the id exists.
     *
     * @param id must not be null.
     * @return true if an entity with the given id exists, false otherwise.
     * @throws IllegalArgumentException in case the given `id` is null.
     */
    suspend fun existsById(id: ID): Boolean

    /**
     * Returns all instances of the type.
     *
     * @return [ReceiveChannel] emitting all entities.
     */
    suspend fun findAll(): List<T>

    /**
     * Returns all instances with the given IDs.
     *
     * @param ids must not be null.
     * @return found entities.
     * @throws IllegalArgumentException in case the given [Iterable] `ids` is null.
     */
    suspend fun findAllById(ids: Iterable<ID>): List<T>

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    suspend fun count(): Long

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be null.
     * @throws IllegalArgumentException in case the given `id` is null.
     */
    suspend fun deleteById(id: ID): Unit

    /**
     * Deletes a given entity.
     *
     * @param entity must not be null.
     * @throws IllegalArgumentException in case the given entity is null.
     */
    suspend fun delete(entity: T): Unit

    /**
     * Deletes the given entities.
     *
     * @param entities must not be null.
     * @throws IllegalArgumentException in case the given [Iterable] `entities` is null.
     */
    suspend fun deleteAll(entities: Iterable<T>): Unit

    /**
     * Deletes all entities managed by the repository.
     *
     */
    suspend fun deleteAll(): Unit
}