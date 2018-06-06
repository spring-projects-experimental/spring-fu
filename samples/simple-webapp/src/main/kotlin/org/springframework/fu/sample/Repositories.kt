package org.springframework.fu.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
import org.springframework.fu.module.data.mongodb.coroutines.count
import org.springframework.fu.module.data.mongodb.coroutines.data.mongodb.core.CoroutineMongoTemplate
import org.springframework.fu.module.data.mongodb.coroutines.findAll
import org.springframework.fu.module.data.mongodb.coroutines.findById
import org.springframework.fu.module.data.mongodb.coroutines.remove

class ReactorUserRepository(private val template: ReactiveMongoTemplate,
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

class CoroutineUserRepository(private val template: CoroutineMongoTemplate,
							  private val objectMapper: ObjectMapper) {

	suspend fun init() {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			save(it)
		}
	}

	suspend fun count() = template.count<User>()

	suspend fun findAll() = template.findAll<User>()

	suspend fun findOne(id: String) = template.findById<User>(id)

	suspend fun deleteAll() = template.remove<User>()

	suspend fun save(user: User) = template.save(user)

}
