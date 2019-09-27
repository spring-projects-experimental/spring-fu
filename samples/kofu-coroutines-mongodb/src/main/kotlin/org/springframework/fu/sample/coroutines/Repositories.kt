package org.springframework.fu.sample.coroutines

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations
import org.springframework.data.mongodb.core.allAndAwait
import org.springframework.data.mongodb.core.asType
import org.springframework.data.mongodb.core.awaitCount
import org.springframework.data.mongodb.core.awaitOne
import org.springframework.data.mongodb.core.findReplaceAndAwait
import org.springframework.data.mongodb.core.flow
import org.springframework.data.mongodb.core.insert
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.query
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.data.mongodb.core.update

class UserRepository(
		private val mongo: ReactiveFluentMongoOperations,
		private val objectMapper: ObjectMapper
) {

	suspend fun count() = mongo.query<User>().awaitCount()

	fun findAll() = mongo.query<User>().flow()

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