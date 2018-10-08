package com.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.*

class UserRepository(
		private val mongo: ReactiveMongoOperations,
		private val objectMapper: ObjectMapper
) {

	fun count() = mongo.count<User>()

	fun findAll() = mongo.findAll<User>()

	fun findOne(id: String) = mongo.findById<User>(id)

	fun deleteAll() = mongo.remove<User>()

	fun save(user: User) = mongo.save(user)

	fun init() {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			save(it).subscribe()
		}

	}

}