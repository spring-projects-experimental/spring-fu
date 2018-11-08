package org.springframework.fu.sample.coroutines

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.*

class UserRepository(
		private val mongo: CoMongoTemplate,
		private val objectMapper: ObjectMapper
) {

	suspend fun count() = mongo.count<User>()

	suspend fun findAll() = mongo.findAll<User>()

	suspend fun findOne(id: String) = mongo.findById<User>(id)

	suspend fun deleteAll() = mongo.remove<User>()

	suspend fun save(user: User) = mongo.save(user)

	suspend fun init() {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			save(it)
		}

	}

}