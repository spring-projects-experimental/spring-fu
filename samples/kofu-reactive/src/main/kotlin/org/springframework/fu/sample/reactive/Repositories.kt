package org.springframework.fu.sample.reactive

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.*

class UserRepository(
	private val template: ReactiveMongoTemplate,
	private val objectMapper: ObjectMapper
) {

	fun count() = template.count<User>()

	fun findAll() = template.findAll<User>()

	fun findOne(id: String) = template.findById<User>(id)

	fun deleteAll() = template.remove<User>()

	fun save(user: User) = template.save(user)

	fun init() {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			save(it).subscribe()
		}

	}

}