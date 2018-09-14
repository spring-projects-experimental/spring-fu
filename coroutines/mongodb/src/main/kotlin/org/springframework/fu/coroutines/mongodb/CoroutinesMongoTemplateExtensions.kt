package org.springframework.fu.coroutines.mongodb

import org.springframework.data.mongodb.core.query.Query
import org.springframework.fu.coroutines.mongodb.data.mongodb.core.CoroutinesMongoTemplate

suspend inline fun <reified T : Any> CoroutinesMongoTemplate.findById(id: Any) =
	findById(id, T::class.java)

suspend inline fun <reified T : Any> CoroutinesMongoTemplate.find(query: Query = Query()) =
	find(query, T::class.java)

suspend inline fun <reified T : Any> CoroutinesMongoTemplate.findAll() =
	findAll(T::class.java)

suspend inline fun <reified T : Any> CoroutinesMongoTemplate.count(query: Query = Query()) =
	count(query, T::class.java)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
suspend inline fun <reified T : Any> CoroutinesMongoTemplate.remove(query: Query = Query()) =
	remove(query, T::class.java)