package com.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.ReactiveRedisTemplate


class UserRepository(
		reactiveRedisTemplate: ReactiveRedisTemplate<String, User>,
		private val objectMapper: ObjectMapper
) {
	private val operations = reactiveRedisTemplate.opsForHash<String, User>()

	fun count() = operations.size(KEY)

	fun findAll() = operations.values(KEY)

	fun findOne(login: String) = operations.get(KEY, login)

	fun save(user: User) = operations.put(KEY, user.login, user)

	fun deleteAll() = operations.delete(KEY)

	fun init() {
		val eventResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventResource.inputStream)
		users.forEach {
			save(it).subscribe()
		}
	}
}