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

package org.springframework.fu.module.data.mongodb.coroutines

import com.mongodb.reactivestreams.client.MongoCollection

/**
 * See [MongoCollection]
 */
interface CoroutineMongoCollection<TDocument> {
    val mongoCollection: MongoCollection<TDocument>
}

data class DefaultCoroutineMongoCollection<TDocument>(
    override val mongoCollection: MongoCollection<TDocument>
): CoroutineMongoCollection<TDocument>

internal fun <T> MongoCollection<T>.asCoroutineMongoCollection(): CoroutineMongoCollection<T> =
		DefaultCoroutineMongoCollection(this)
