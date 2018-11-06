/*
 * Copyright 2002-2018 the original author or authors.
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

import org.springframework.data.mongodb.core.query.Query

suspend inline fun <reified T : Any> CoMongoTemplate.findById(id: Any) =
	findById(id, T::class.java)

suspend inline fun <reified T : Any> CoMongoTemplate.find(query: Query = Query()) =
	find(query, T::class.java)

suspend inline fun <reified T : Any> CoMongoTemplate.findAll() =
	findAll(T::class.java)

suspend inline fun <reified T : Any> CoMongoTemplate.count(query: Query = Query()) =
	count(query, T::class.java)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
suspend inline fun <reified T : Any> CoMongoTemplate.remove(query: Query = Query()) =
	remove(query, T::class.java)