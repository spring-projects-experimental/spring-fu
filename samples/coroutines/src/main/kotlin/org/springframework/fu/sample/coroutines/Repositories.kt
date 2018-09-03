package org.springframework.fu.sample.coroutines

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.fu.coroutines.mongodb.count
import org.springframework.fu.coroutines.mongodb.data.mongodb.core.CoroutineMongoTemplate
import org.springframework.fu.coroutines.mongodb.findAll
import org.springframework.fu.coroutines.mongodb.findById
import org.springframework.fu.coroutines.mongodb.remove

class UserRepository(
		private val template: CoroutineMongoTemplate,
		private val objectMapper: ObjectMapper
) {

	suspend fun count() = template.count<User>()

	suspend fun findAll() = template.findAll<User>()

	suspend fun findOne(id: String) = template.findById<User>(id)

	suspend fun deleteAll() = template.remove<User>()

	suspend fun save(user: User) = template.save(user)

	suspend fun init() {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			save(it)
		}

	}

}