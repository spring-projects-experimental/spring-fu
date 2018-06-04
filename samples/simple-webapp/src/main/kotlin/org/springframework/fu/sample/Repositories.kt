package org.springframework.fu.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query

class UserRepository(private val template: ReactiveMongoTemplate,
					 objectMapper: ObjectMapper) {

	init {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach { save(it).subscribe() }
	}

	fun count() = template.count<User>()

	fun findAll() = template.find<User>(Query())

	fun findOne(id: String) = template.findById<User>(id)

	fun deleteAll() = template.remove<User>()

	fun save(user: User) = template.save(user)

}