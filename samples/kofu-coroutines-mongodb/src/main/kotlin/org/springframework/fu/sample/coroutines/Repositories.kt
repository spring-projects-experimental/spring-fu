package org.springframework.fu.sample.coroutines

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Criteria.*
import org.springframework.data.mongodb.core.query.Query.*
import org.springframework.data.mongodb.core.query.isEqualTo

class UserRepository(
		private val mongo: ReactiveFluentMongoOperations,
		private val objectMapper: ObjectMapper
) {

	suspend fun count() = mongo.query<User>().awaitCount()

	suspend fun findAll() = mongo.query<User>().all().collectList().awaitSingle()

	suspend fun findOne(id: String) = mongo.query<User>().matching(query(where("id").isEqualTo(id))).awaitOne()

	suspend fun deleteAll() {
		mongo.remove<User>().allAndAwait()
	}

	suspend fun insert(user: User) = mongo.insert<User>().oneAndAwait(user)

	suspend fun update(user: User) = mongo.update<User>().replaceWith(user).asType<User>().findReplaceAndAwait()!!

	suspend fun init() {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			insert(it)
		}

	}

}